package com.microservices.authorizationservice.controller;

import java.util.Set;

import com.microservices.authorizationservice.common.ApiResponse;
import com.microservices.authorizationservice.common.CursorResponse;
import com.microservices.authorizationservice.common.PaginatedResponse;
import com.microservices.authorizationservice.customexception.ResourceNotFoundException;
import com.microservices.authorizationservice.entity.AccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.service.AccessService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
@Validated
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authorization/access")

public class AccessController {

	@Autowired
	AccessService accessService;

	@GetMapping("/check-duplicate")
	public ResponseEntity<ApiResponse<?>> isDuplicateAccess(@RequestParam("accessName") String accessName) {

		if (accessName == null || accessName.trim().isEmpty()) {
			throw new IllegalArgumentException("Access name cannot be null or empty");
		}

		boolean isDuplicate = accessService.isDuplicateAccess(accessName);

		return ResponseEntity.ok(
				new ApiResponse<>(true, "Check completed", isDuplicate)
		);

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
			@RequestParam(defaultValue = "0")        int page,
			@RequestParam(defaultValue = "10")       int size,
			@RequestParam(required = false)          String searchText,
			@RequestParam(defaultValue = "accessId",required = false) String sortBy,
			@RequestParam(defaultValue = "asc",required = false)      String direction) {

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
					return ResponseEntity.ok().body(true);
				} else {
					return ResponseEntity.internalServerError().body("Access Updation Failed");
				}

		} else {
			return ResponseEntity.badRequest().body("Access id should be greater than zero");

		}
	}

	@DeleteMapping("delete/{accessId}")
	public ResponseEntity<?> deleteAccess(@PathVariable("accessId") int accessId) {
		try {
			if (accessId > 0) {
				if (accessService.deleteAccess(accessId)) {
					return ResponseEntity.ok().body(true);
				} else {
					return ResponseEntity.internalServerError().body("Access deletion failed");
				}
			} else {
				return ResponseEntity.badRequest().body("Access id should be greater than zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}

	}

	@GetMapping("list-all")
	public ResponseEntity<?> getAllAccesses() {
		try {
			Set<AccessBo> accessPage = accessService.listAllAccesses();
			if (accessPage != null) {
				return ResponseEntity.ok(accessPage);
			} else {
				return ResponseEntity.internalServerError().body("Error while fetching set of Accesses");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

//	@GetMapping("get-duplicate-list")
//	public ResponseEntity<?> getAllAccessesDetails() {
//
//		try {
//			Set<AccessBo> acessPage = accessService.listAllAccesses();
//
//			if (acessPage != null) {
//				return ResponseEntity.ok(acessPage);
//			}
//
//		} catch (Exception e) {
//
//		}
//
//		return null;
//
//	}

}
