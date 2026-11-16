package com.clinch.crm.authorization.controller;

import com.clinch.crm.authorization.common.ApiResponse;
import com.clinch.crm.authorization.repository.RoleRepository;
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

import com.clinch.crm.authorization.model.RoleBo;
import com.clinch.crm.authorization.service.RoleService;

import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * REST endpoints for <b>roles</b>, under {@code /authorization/role} (JWT required at the gateway).
 *
 * <p>A role (e.g. {@code SALES_MANAGER}) is a named bundle of privileges. Users carry role names;
 * at sign-in those names are turned into privileges (see {@code PrivilegeController#getPrivilegesForRoles}).
 *
 * <p>Unlike the other controllers, most endpoints here return <b>raw bodies</b> ({@code true},
 * a plain string, a {@code RoleBo} or a Spring {@code Page}) instead of the {@link ApiResponse}
 * envelope; only {@link #roleExists} uses it. Most service methods return {@code false}/{@code null}
 * on any failure, which these endpoints turn into 500, so "not found" also shows up as 500.
 */
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/authorization/role")
@Log4j2
public class RoleController {
	/** Used directly by {@link #roleExists}. */
	@Autowired
	RoleRepository roleRepository;

	/** Role business logic. */
	@Autowired
	RoleService roleService;

	/**
	 * {@code GET /authorization/role/check-duplicate?roleName=...}: is the name already used by a
	 * role that isn't soft-deleted?
	 *
	 * @param roleName the name to check (exact, case as stored by MySQL's collation)
	 * @return <b>409</b> with body {@code true} if taken; 200 {@code false} if free;
	 *         400 if the name is empty
	 */
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

	/**
	 * {@code POST /authorization/role/create}: creates an active role with the given privileges.
	 *
	 * <p>Body: {@code {"roleName": "SALES_MANAGER", "privileges": [{"privilegeId": 1}, ...]}}.
	 * Only {@code privilegeId} is used from each privilege. At least one privilege is required;
	 * without one, nothing is saved. The name is <b>not</b> checked for duplicates here; call
	 * {@link #isDuplicateRole} first.
	 *
	 * @param role          the role to create
	 * @param bindingResult validation result for {@code role} (filled because of {@code @Validated})
	 * @return 201 {@code true}; 400 with the list of validation errors; 500 "Role creation Failed"
	 *         if there are no privileges or a privilege id doesn't exist
	 */
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

	/**
	 * {@code GET /authorization/role/view/{roleId}}: one role with its privileges (sorted by name).
	 *
	 * <p>Soft-deleted roles are returned too.
	 *
	 * @param roleId the role id, must be greater than 0
	 * @return 200 with the {@link RoleBo}; 404 "Role not found"; 400 for an id of 0 or less
	 */
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

	/**
	 * {@code GET /authorization/role/list/{pageIndex}/{pageSize}?sortOrder=&searchText=}: one page
	 * of roles that aren't soft-deleted.
	 *
	 * <p>The body is a serialized Spring {@code Page} ({@code content}, {@code totalElements},
	 * {@code totalPages}, {@code number}, …). Items don't include privileges.
	 *
	 * @param pageIndex  zero-based page number, 0 or more
	 * @param pageSize   items per page, more than 0
	 * @param sortOrder  {@code asc} sorts by name ascending, any other value descending; absent
	 *                   sorts by id
	 * @param searchText optional case-insensitive "name contains" filter
	 * @return 200 with the page; 400 for an invalid index/size
	 */
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

	/**
	 * {@code PUT /authorization/role/update}: replaces a role's name and privileges.
	 *
	 * <p>Body: {@code {"roleId": 1, "roleName": "...", "privileges": [{"privilegeId": 1,
	 * "privilegeName": "..."}]}}. The active and deleted flags are kept as stored; any
	 * {@code active} value in the body is ignored (use {@link #switchActivity}). Every privilege
	 * needs {@code privilegeName} (the set is sorted by it), and at least one privilege is required.
	 *
	 * @param role          the new state, including {@code roleId}
	 * @param bindingResult validation result for {@code role}
	 * @return 200 {@code true}; 400 for a missing id or validation errors; 500 "Role Updation Failed"
	 *         if the role doesn't exist or there are no privileges
	 */
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

	/**
	 * {@code PATCH /authorization/role/switch-activity/{roleId}}: toggles active ↔ inactive.
	 *
	 * <p>Inactive roles grant no privileges at the next sign-in (tokens already issued keep theirs
	 * until they expire). Browsers can't call this through the gateway yet, because the gateway's
	 * CORS config doesn't allow PATCH.
	 *
	 * @param roleId the role id, must be greater than 0
	 * @return 200 with a JSON string {@code {"message": "Role activity switched successfully "}};
	 *         400 for an id of 0 or less; 500 if the role doesn't exist
	 */
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

	/**
	 * {@code DELETE /authorization/role/delete/{roleId}}: soft delete (sets {@code isDeleted}; the
	 * row stays). Deleted roles grant no privileges at sign-in and disappear from lists.
	 *
	 * @param roleId the role id, must be greater than 0
	 * @return 200 {@code true}; 400 for an id of 0 or less; 500 if the role doesn't exist
	 */
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


	/**
	 * {@code POST /authorization/role/bulk}: creates several roles, e.g. to seed a database.
	 *
	 * <p>Each item is handled like {@link #createRole}, but without validation; failures are
	 * skipped silently.
	 *
	 * @param roleBos the roles to create
	 * @return 201 with a text such as "3 of 4 roles created"
	 */
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


	/**
	 * {@code GET /authorization/role/exists?roleName=...}: called by employee-service (Feign)
	 * before it assigns a role to a new employee.
	 *
	 * <p>Note: it matches <b>any</b> role with that name, including inactive and soft-deleted ones.
	 *
	 * @param roleName the exact role name
	 * @return {@link ApiResponse} with {@code data} {@code true} if a role with this name exists
	 */
	@GetMapping("/exists")
	public ApiResponse<Boolean> roleExists(@RequestParam("roleName") String roleName) {
		return ApiResponse.success("Role check", roleRepository.existsByRoleName(roleName));
	}


}
