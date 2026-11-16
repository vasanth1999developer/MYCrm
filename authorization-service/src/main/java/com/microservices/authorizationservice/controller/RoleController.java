package com.microservices.authorizationservice.controller;

import com.microservices.authorizationservice.common.ApiResponse;
import com.microservices.authorizationservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.authorizationservice.model.RoleBo;
import com.microservices.authorizationservice.service.RoleService;

import lombok.extern.log4j.Log4j2;

import java.util.List;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authorization/role")
@Log4j2
public class RoleController {
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	RoleService roleService;

	@GetMapping("/check-duplicate")
	public ResponseEntity<?> isDuplicateRole(@RequestParam("roleName") String roleName) {
		try {
			log.info("ROLE CONTROLLER | check-duplicate | roleName :  " + roleName);
			if ((roleName != null) && (!roleName.isEmpty())) {
				if (roleService.isDuplicateRole(roleName)) {
					return ResponseEntity.status(HttpStatus.CONFLICT).body(true);
				} else {
					return ResponseEntity.ok().body(false);
					
				}
			} else {
				return ResponseEntity.badRequest().body("Role name cannot be null or empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while checking for duplicate role", e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> createRole(@Validated @RequestBody RoleBo role, BindingResult bindingResult) {
		try {
			if (bindingResult.hasErrors()) {
				return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
			} else {
				if (roleService.createRole(role)) {
					return ResponseEntity.status(HttpStatus.CREATED).body(true);
				} else {
					return ResponseEntity.internalServerError().body("Role creation Failed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while creating the role", e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@GetMapping("/view/{roleId}")
	public ResponseEntity<?> getRoleByRoleId(@PathVariable("roleId") int roleId) {
		try {
			if (roleId > 0) {
				RoleBo role = roleService.getRoleByRoleId(roleId);
				if (role != null) {
					return ResponseEntity.ok(role);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
				}
			} else {
				return ResponseEntity.badRequest().body("Role id should be greater than zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while viewing the role", e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@GetMapping("/list/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listRoles(@PathVariable("pageIndex") int pageIndex, @PathVariable("pageSize") int pageSize,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			if ((pageIndex >= 0) && (pageSize > 0)) {
				Page<RoleBo> rolePage = roleService.listRoles(pageIndex, pageSize, sortOrder, searchText);
				if (rolePage != null) {
					return ResponseEntity.ok(rolePage);
				} else {
					return ResponseEntity.internalServerError().body("Error while fetching set of roles");
				}
			} else {
				return ResponseEntity.badRequest().body("Invalid page index or size");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while listing the roles", e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateRole(@Validated @RequestBody RoleBo role, BindingResult bindingResult) {
		try {
			if ((role!=null)&&(role.getRoleId() > 0)) {
				if (bindingResult.hasErrors()) {
					return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
				} else {
					if (roleService.updateRole(role)) {
						return ResponseEntity.ok().body(true);
					} else {
						return ResponseEntity.internalServerError().body("Role Updation Failed");
					}
				}
			} else {
				return ResponseEntity.badRequest().body("Role id should be greater than zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while updating the role", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@PatchMapping("switch-activity/{roleId}")
	public ResponseEntity<?> switchActivity(@PathVariable("roleId") int roleId) {
		try {
			if (roleId > 0) {
				if (roleService.switchActivity(roleId)) {				
					return ResponseEntity.ok().body("{\"message\": \"" + "Role activity switched successfully " + "\"}");
				} else {
					return ResponseEntity.internalServerError().body("Error occurred while switching role activity");
				}
			} else {
				return ResponseEntity.badRequest().body("Role id should be greater than zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while switching the active status of the role", e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@DeleteMapping("delete/{roleId}")
	public ResponseEntity<?> deleteRole(@PathVariable("roleId") int roleId) {
		try {
			if (roleId > 0) {
				if (roleService.deleteRole(roleId)) {
					return ResponseEntity.ok().body(true);
				} else {
					return ResponseEntity.internalServerError().body("Role deletion failed");
				}		
			} else {
				return ResponseEntity.badRequest().body("Role id should be greater than zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occurred while deleting the role", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}


	@PostMapping("/bulk")
	public ResponseEntity<String> createRoles(@RequestBody List<RoleBo> roleBos) {
		int created = 0;
		for (RoleBo bo : roleBos) {
			if (roleService.createRole(bo)) {
				created++;
			}
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(created + " of " + roleBos.size() + " roles created");
	}


	@GetMapping("/exists")
	public ApiResponse<Boolean> roleExists(@RequestParam("roleName") String roleName) {
		return ApiResponse.success("Role check", roleRepository.existsByRoleName(roleName));
	}


}
