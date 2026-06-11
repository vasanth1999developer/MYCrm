package com.microservices.authorizationservice.controller;

import com.microservices.authorizationservice.common.ApiResponse;
import com.microservices.authorizationservice.common.PaginatedResponse;
import com.microservices.authorizationservice.customexception.ResourceNotFoundException;
import com.microservices.authorizationservice.entity.AccessVo;
import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Set;

@Validated
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authorization/access")

public class AccessController {


    @Autowired
    AccessService accessService;

    @GetMapping("/check-duplicate")
    public ResponseEntity<ApiResponse<Boolean>> isDuplicateAccess(@RequestParam("accessName") String accessName) {

        if (accessName == null || accessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Access name cannot be null or empty");
        }

        boolean isDuplicate = accessService.isDuplicateAccess(accessName);

        if (isDuplicate) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Duplicate is there..", true), HttpStatus.CONFLICT
            );

        } else {
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Access name is available", false)
            );
        }

    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AccessVo>> createAccess(
            @Valid @RequestBody AccessBo accessBo) {

        AccessVo savedAccess = accessService.createAccess(accessBo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Access created successfully", savedAccess));
    }

    @GetMapping("/get-access/{accessId}")
    public ResponseEntity<?> getAccess(
            @PathVariable @Min(value = 1, message = "Access id should be greater than zero") int accessId) {

        AccessBo access = accessService.getAccessByAccessId(accessId);

        if (access == null) {
            throw new ResourceNotFoundException("Access not found");
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Access fetched", access)
        );
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<AccessBo>>> listAccesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "accessId", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String direction) {

        if (size <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Size must be greater than 0", null));
        }

        if (size > 50) size = 50;

        PaginatedResponse<AccessBo> response =
                accessService.getAccess(page, size, searchText, sortBy, direction);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Access fetched successfully", response)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateAccess(@Validated @RequestBody AccessBo access, BindingResult bindingResult) {

        if ((access != null) && (access.getAccessId() > 0)) {

            if (accessService.updateAccess(access)) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Access is updated Successfully"));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(true, "Access updated Failed"));
            }

        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Id Should be Greter than 0"));

        }
    }

    @DeleteMapping("delete/{accessId}")
    public ResponseEntity<?> deleteAccess(@PathVariable("accessId") int accessId) {
        try {
            if (accessId > 0) {
                if (accessService.deleteAccess(accessId)) {
                    return ResponseEntity.ok(new ApiResponse<>(true, "Access is Deleted"));
                } else {
                    return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Access Deletion is Failed"));
                }
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Id Should be Greter than 0"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "InternalServer Error"));
        }

    }

    @GetMapping("list-all")
    public ResponseEntity<?> getAllAccesses() {
        try {
            Set<AccessBo> accessPage = accessService.listAllAccesses();
            if (accessPage != null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "fetched Successfully", accessPage));
            } else {
                return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Error while fetching set of Accesses"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Internal Server Error"));
        }
    }


}
