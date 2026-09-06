package com.microservices.authorizationservice.controller;

import com.microservices.authorizationservice.common.ApiResponse;
import com.microservices.authorizationservice.model.PrivilegeBo;
import com.microservices.authorizationservice.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@CrossOrigin(origins="*")
@RestController
@RequestMapping("/authorization/privilege")
public class PrivilegeController {

    @Autowired
    public PrivilegeService service;

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

}
