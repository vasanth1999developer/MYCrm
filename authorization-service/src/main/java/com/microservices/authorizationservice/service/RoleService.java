package com.microservices.authorizationservice.service;

import org.springframework.data.domain.Page;

import com.microservices.authorizationservice.model.RoleBo;

/**
 * Business logic for roles. Implemented by {@link RoleServiceImpl}.
 *
 * <p>Most methods report failure with {@code false}/{@code null} instead of throwing, so callers
 * can't tell "not found" from other errors.
 */
public interface RoleService {

	/**
	 * @param roleName the exact name
	 * @return {@code true} if a role with this name exists and isn't soft-deleted
	 */
	boolean isDuplicateRole(String roleName);

	/**
	 * Creates an active role linked to existing privileges (by id).
	 *
	 * @param role name and privileges (at least one)
	 * @return {@code true} if saved; {@code false} if there are no privileges, an id doesn't exist,
	 *         or anything else fails
	 */
	boolean createRole(RoleBo role);

	/**
	 * @param roleId the id
	 * @return the role with its privileges sorted by name, or {@code null} if not found
	 */
	RoleBo getRoleByRoleId(int roleId);

	/**
	 * One page of roles that aren't soft-deleted (without their privileges).
	 *
	 * @param pageIndex  zero-based page number
	 * @param pageSize   items per page
	 * @param sortOrder  {@code asc}, another value for descending by name, or {@code null}/empty for by id
	 * @param searchText optional case-insensitive "name contains" filter
	 * @return the page, or {@code null} on error
	 */
	Page<RoleBo> listRoles(int pageIndex,int pageSize,String sortOrder,String searchText);

	/**
	 * Replaces the name and privileges of an existing role; the active and deleted flags are kept.
	 *
	 * @param role the new state, including {@code roleId}
	 * @return {@code true} if saved; {@code false} if the role doesn't exist, has no privileges, or
	 *         anything fails
	 */
	boolean updateRole(RoleBo role);

	/**
	 * Soft-deletes a role.
	 *
	 * @param roleId the id
	 * @return {@code true} if marked deleted; {@code false} if not found or on error
	 */
	boolean deleteRole(int roleId);

	/**
	 * Toggles a role between active and inactive.
	 *
	 * @param roleId the id
	 * @return {@code true} if switched; {@code false} if not found or on error
	 */
	boolean switchActivity(int roleId);

}
