package com.clinch.crm.authorization.controller;

import com.clinch.crm.authorization.common.ApiResponse;
import com.clinch.crm.authorization.common.PaginatedResponse;
import com.clinch.crm.authorization.customexception.ResourceNotFoundException;
import com.clinch.crm.authorization.entity.AccessVo;
import com.clinch.crm.authorization.model.AccessBo;
import com.clinch.crm.authorization.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;

/**
 * REST endpoints for <b>access entries</b>, under {@code /authorization/access} (JWT required at
 * the gateway).
 *
 * <p>An access entry (e.g. "view leads") is the smallest unit in the RBAC model:
 * role → privileges → accesses. All responses use the {@link ApiResponse} envelope.
 *
 * <p>{@code @Validated} on the class enables constraints on path/query parameters such as
 * {@code @Min}; violations become 400 through {@code GlobalExceptionHandler}.
 */
@Validated
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authorization/access")

public class AccessController {


    /** Access business logic, injected through the constructor. */
    private final AccessService accessService;

    /** @param accessService access business logic */
    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }


    /**
     * {@code POST /authorization/access/create}: creates an access entry.
     *
     * <p>Body: {@code {"accessName": "VIEW_LEADS"}}. The response's {@code data} is the saved
     * <b>entity</b>, so its keys are {@code accessId}, {@code accessName} and {@code deleted}.
     *
     * @param accessBo the access to create; {@code accessName} must not be empty
     * @return 201 with the saved entry; 400 if the name is empty; 409 if a non-deleted entry with
     *         this name exists
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AccessVo>> createAccess(
            @Valid @RequestBody AccessBo accessBo) {

        AccessVo savedAccess = accessService.createAccess(accessBo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body( ApiResponse.success( "Access created successfully", savedAccess));
    }

    /**
     * {@code GET /authorization/access/get-access/{accessId}}: one access entry (soft-deleted ones too).
     *
     * @param accessId the id, at least 1
     * @return 200 with the entry; also <b>200</b> ({@code success: true}, no data,
     *         "pls Enter Proper Access Id") if it doesn't exist; 400 for an id below 1
     */
    @GetMapping("/get-access/{accessId}")
    public ResponseEntity<?> getAccess(
            @PathVariable @Min(value = 1, message = "Access id should be greater than zero") int accessId) {
        AccessBo access = accessService.getAccessByAccessId(accessId);

        if (access == null) {
            return ResponseEntity.ok(
                    ApiResponse.success( "pls Enter Proper Access Id" )
            );
        }

        return ResponseEntity.ok(
                 ApiResponse.success( "Access fetched", access)
        );
    }


    /**
     * {@code GET /authorization/access/list?page=0&size=10&searchText=&sortBy=accessId&direction=asc}:
     * one page of access entries that aren't soft-deleted.
     *
     * <p>{@code data} is a {@link PaginatedResponse}: {@code data}, {@code currentPage},
     * {@code totalPages}, {@code totalItems}, {@code first}, {@code last}, {@code hasNext},
     * {@code hasPrevious}.
     *
     * @param page       zero-based page number (negative becomes 0)
     * @param size       items per page; must be above 0, capped at 50
     * @param searchText optional case-insensitive "name contains" filter
     * @param sortBy     {@code accessId} or {@code accessName}; any other value currently causes 500
     * @param direction  {@code desc} for descending, anything else ascending
     * @return 200 with the page; 400 if {@code size} is 0 or less
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<AccessBo>>> listAccesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "accessId", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String direction) {

        if (size <= 0) {
            return ResponseEntity.badRequest()
                    .body( ApiResponse.error( "Size must be greater than 0", null));
        }

        if (size > 50) size = 50;

        PaginatedResponse<AccessBo> response =
                accessService.getAccess(page, size, searchText, sortBy, direction);

        return ResponseEntity.ok(
                 ApiResponse.success( "Access fetched successfully", response)
        );
    }

    /**
     * {@code PUT /authorization/access/update}: renames an access entry.
     *
     * <p>Body: {@code {"accessId": 1, "accessName": "..."}}. Validation errors are collected in
     * {@code bindingResult} but not checked, so an empty name is saved as is.
     *
     * @param access        the id and the new name
     * @param bindingResult validation result (currently ignored)
     * @return 200 "Access is updated Successfully"; 400 for an id of 0 or less; 500 if the id
     *         doesn't exist ({@code ResourceNotFoundException} has no dedicated handler)
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateAccess(@Validated @RequestBody AccessBo access, BindingResult bindingResult) {

        if ((access != null) && (access.getAccessId() > 0)) {

            if (accessService.updateAccess(access)) {
                return ResponseEntity.ok( ApiResponse.success( "Access is updated Successfully"));
            } else {
                return ResponseEntity.ok( ApiResponse.error( "Access updated Failed"));
            }

        } else {
            return ResponseEntity.badRequest().body( ApiResponse.error( "Id Should be Greter than 0"));

        }
    }

    /**
     * {@code DELETE /authorization/access/delete/{accessId}}: soft delete (sets {@code isDeleted}).
     *
     * @param accessId the id, must be greater than 0
     * @return 200 "Access is Deleted"; 400 for an id of 0 or less; 500 if it doesn't exist
     */
    @DeleteMapping("delete/{accessId}")
    public ResponseEntity<?> deleteAccess(@PathVariable("accessId") int accessId) {
        try {
            if (accessId > 0) {
                if (accessService.deleteAccess(accessId)) {
                    return ResponseEntity.ok( ApiResponse.success( "Access is Deleted"));
                } else {
                    return ResponseEntity.internalServerError().body( ApiResponse.error("Access Deletion is Failed"));
                }
            } else {
                return ResponseEntity.badRequest().body( ApiResponse.error( "Id Should be Greter than 0"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( ApiResponse.error( "InternalServer Error"));
        }

    }

    /**
     * {@code GET /authorization/access/list-all}: every access entry that isn't soft-deleted,
     * sorted by name, without paging.
     *
     * @return 200 with the set; <b>500</b> when there are none at all (the service returns
     *         {@code null} for an empty table)
     */
    @GetMapping("list-all")
    public ResponseEntity<?> getAllAccesses() {
        try {
            Set<AccessBo> accessPage = accessService.listAllAccesses();
            if (accessPage != null) {
                return ResponseEntity.ok( ApiResponse.success( "fetched Successfully", accessPage));
            } else {
                return ResponseEntity.internalServerError().body( ApiResponse.error( "Error while fetching set of Accesses"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body( ApiResponse.error( "Internal Server Error"));
        }
    }


}
