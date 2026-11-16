package com.clinch.crm.authorization.controller;

import com.clinch.crm.authorization.common.ApiResponse;
import com.clinch.crm.authorization.model.PrivilegeBo;
import com.clinch.crm.authorization.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * REST endpoints for <b>privileges</b>, under {@code /authorization/privilege} (JWT required at
 * the gateway).
 *
 * <p>A privilege (e.g. {@code LEAD_READ}) is a named permission made of access entries. Its name
 * is what ends up in the JWT and in the {@code X-User-Privileges} header.
 *
 * <p>The most important endpoint is {@link #getPrivilegesForRoles}, called by
 * authentication-service at every sign-in.
 */
//@CrossOrigin(origins="*")
@RestController
@RequestMapping("/authorization/privilege")
public class PrivilegeController {

    /** Privilege business logic. */
    @Autowired
    public PrivilegeService service;

    /**
     * {@code GET /authorization/privilege/check-duplicate?privilegeName=...}: is the name already
     * used by a privilege that isn't soft-deleted?
     *
     * <p>Both outcomes use {@code ApiResponse.error} (so {@code success} is {@code false} even when
     * the name is free); tell them apart by the HTTP status.
     *
     * @param privilegeName the name to check
     * @return 409 "Duplicate is there.." if taken; 200 "privilege name is available" if free;
     *         400 (via {@code GlobalExceptionHandler}) if the name is blank
     */
    @GetMapping("check-duplicate")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicateName(@RequestParam("privilegeName") String privilegeName) {

        if (privilegeName == null || privilegeName.trim().isEmpty()) {
            throw new IllegalArgumentException("privilege name cannot be null or empty");

        }

        boolean status = service.checkDuplicateName(privilegeName);

        if (status) {
            return new ResponseEntity<>(
                     ApiResponse.error( "Duplicate is there.."),HttpStatus.CONFLICT
            );
        } else {
            return ResponseEntity.ok(
                     ApiResponse.error( "privilege name is available")
            );
        }

    }

    /**
     * {@code POST /authorization/privilege/create}: creates a privilege linked to existing access entries.
     *
     * <p>Body: {@code {"privilegeName": "LEAD_READ", "accesses": [{"accessId": 1}, ...]}}. Only
     * {@code accessId} is used from each access. At least one access is required; without one
     * nothing is saved. The name isn't checked for duplicates here.
     *
     * @param privilegeBo the privilege to create
     * @return 201 with the saved privilege; 500 "privilege creation is failed" if there are no
     *         accesses or an access id doesn't exist
     */
    @PostMapping("create")
    public ResponseEntity<ApiResponse<PrivilegeBo>> createPrivilege(@Valid @RequestBody PrivilegeBo privilegeBo) {


        PrivilegeBo privilegeBoResponse = service.createPrivilege(privilegeBo);

        if (privilegeBoResponse == null){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body( ApiResponse.error( "privilege creation is failed"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body( ApiResponse.success( "privilege created successfully", privilegeBoResponse));
    }

    /**
     * {@code GET /authorization/privilege/list/{pageIndex}/{pageSize}?sortOrder=&searchText=}:
     * one page of privileges that aren't soft-deleted, as a raw Spring {@code Page}.
     *
     * <p>Known quirks of the current logic:
     * <ul>
     *   <li>{@code /list/0/10} with no query parameters skips the lookup and returns
     *       <b>204 No Content</b>.</li>
     *   <li>{@code sortOrder} without {@code searchText} fails inside the service and also gives 204.</li>
     *   <li>The search filter is applied only when {@code sortOrder} is also given.</li>
     * </ul>
     * In practice, pass both, e.g. {@code /list/0/10?sortOrder=asc&searchText=lead}.
     *
     * @param pageIndex  zero-based page number
     * @param pageSize   items per page
     * @param sortOrder  {@code asc} sorts by name ascending, any other value descending
     * @param searchText case-insensitive "name contains" filter
     * @return 200 with the page, or 204 when empty or on error
     */
    @GetMapping("list/{pageIndex}/{pageSize}")
    public ResponseEntity<Page<PrivilegeBo>> listPrivilege(@PathVariable("pageIndex") int pageIndex,
                                                           @PathVariable("pageSize") int pageSize, @RequestParam(name = "sortOrder", required = false) String sortOrder,
                                                           @RequestParam(name = "searchText", required = false) String searchText) {
        Page<PrivilegeBo> privilegeList = null;
        try {
            if (pageIndex > 0 || pageSize > 0 && sortOrder != null || null != searchText) {
                privilegeList = service.listAllPrivileges(pageIndex, pageSize, sortOrder, searchText);
                if (!privilegeList.isEmpty() && privilegeList != null) {
                    return new ResponseEntity<Page<PrivilegeBo>>(privilegeList, HttpStatus.OK);
                } else {
                    return new ResponseEntity<Page<PrivilegeBo>>(privilegeList, HttpStatus.NO_CONTENT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Page<PrivilegeBo>>(privilegeList, HttpStatus.NO_CONTENT);
    }


    /**
     * {@code GET /authorization/privilege/view/{privilegeId}}: one privilege with its accesses.
     *
     * <p><b>Bug:</b> the id check is inverted ({@code privilegeId > 0} is rejected), so every real
     * id gets 400 "Please provide a valid Privilege ID", and ids of 0 or less get 404. The endpoint
     * can't currently return a privilege.
     *
     * @param privilegeId the privilege id
     * @return 400 for any positive id; 404 otherwise (see above)
     */
    @GetMapping("view/{privilegeId}")
    public ResponseEntity<ApiResponse<PrivilegeBo>> viewPrivilege(@PathVariable("privilegeId") int privilegeId) {


            if (privilegeId > 0) {
                return ResponseEntity.badRequest().body( ApiResponse.error("Please provide a valid Privilege ID"));

            }

           PrivilegeBo  privilegeBo = service.findIdByPrivilege(privilegeId);
        if (null != privilegeBo) {
            return ResponseEntity.ok( ApiResponse.success( "fetched successfully", privilegeBo));
        }
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body( ApiResponse.error("Privilege is not fount for the ID "+privilegeId));

    }

    /**
     * {@code PUT /authorization/privilege/update}: replaces a privilege's accesses.
     *
     * <p>Only the {@code accesses} set is changed; {@code privilegeName} in the body is ignored.
     * Each access needs {@code accessId} and {@code accessName}; at least one is required.
     *
     * @param privilegeBo the privilege id and its new accesses
     * @return 200 {@code true} on success, <b>200 {@code false}</b> on failure (missing privilege,
     *         no accesses); 500 if the body is missing
     */
    @PutMapping("update")
    public ResponseEntity<?> updatePrivilege(@RequestBody PrivilegeBo privilegeBo) {
        try {
            if (privilegeBo != null) {
                if (service.updatePrivilege(privilegeBo)) {
                    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                } else {
                    return new ResponseEntity<Boolean>(false, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
        }
        return ResponseEntity.internalServerError().body("internal server error");
    }

    /**
     * {@code DELETE /authorization/privilege/delete/{privilegeId}}: soft delete (sets {@code isDelete}).
     *
     * <p>A deleted privilege disappears from the lists, but it is <b>still granted at sign-in</b>
     * to roles that reference it, because {@link #getPrivilegesForRoles} doesn't filter on it.
     *
     * @param privilegeId the privilege id
     * @return 200 {@code true}; 204 if it doesn't exist; 500 for an id of 0 or less
     */
    @DeleteMapping("delete/{privilegeId}")
    public ResponseEntity<?> deletePrivilege(@PathVariable("privilegeId") int privilegeId) {

        try {
            if (privilegeId > 0) {
                if (service.deletePrivilege(privilegeId)) {
                    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                } else {
                    return new ResponseEntity<Boolean>(false, HttpStatus.NO_CONTENT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().body("internal server error");
    }

    /**
     * {@code GET /authorization/privilege/list-all}: every privilege that isn't soft-deleted,
     * sorted by name, as a raw JSON array (without accesses). Useful to fill a dropdown.
     *
     * @return 200 with the array; <b>500</b> when there are no privileges at all (the service
     *         returns {@code null} for an empty table)
     */
    @GetMapping("list-all")
    public ResponseEntity<?> getAllPrivileges() {
        try {
            Set<PrivilegeBo> privileges = service.listAllPrivileges();
            if (privileges != null) {
                return ResponseEntity.ok(privileges);
            } else {
                return ResponseEntity.internalServerError().body("Error while fetching set of privileges");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Internal Server Error");
        }
    }


    /**
     * {@code POST /authorization/privilege/bulk}: creates several privileges, e.g. to seed a database.
     *
     * <p>Each item is handled like {@link #createPrivilege}, without validation; failures are skipped.
     *
     * @param privilegeBos the privileges to create
     * @return 201 with the privileges that were created
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<PrivilegeBo>> createPrivileges(@RequestBody List<PrivilegeBo> privilegeBos) {
        List<PrivilegeBo> saved = new ArrayList<>();
        for (PrivilegeBo bo : privilegeBos) {
            PrivilegeBo result = service.createPrivilege(bo);
            if (result != null) {
                saved.add(result);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * {@code POST /authorization/privilege/privileges}: <b>used at every sign-in</b> by
     * authentication-service (Feign) to turn role names into privilege names for the JWT.
     *
     * <p>Body: a JSON array of role names, e.g. {@code ["SALES_REP","SALES_MANAGER"]}. Only roles
     * that are active and not soft-deleted count. The result is the sorted union of their privileges.
     *
     * @param roleNames role names from the user's account
     * @return 200 with {@code data} = privilege names (empty if no role matched; authentication-service
     *         then refuses the sign-in)
     */
    @PostMapping("/privileges")
    public ResponseEntity<ApiResponse<Set<String>>> getPrivilegesForRoles(
            @RequestBody Set<String> roleNames) {
        Set<String> privileges = service.getPrivilegeNamesByRoleNames(roleNames);
        return ResponseEntity.ok(
                ApiResponse.success("Privileges fetched", privileges));
    }


}
