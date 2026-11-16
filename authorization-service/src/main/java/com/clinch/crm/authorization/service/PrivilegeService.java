package com.clinch.crm.authorization.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.clinch.crm.authorization.model.AccessBo;
import com.clinch.crm.authorization.model.PrivilegeBo;

/**
 * Business logic for privileges, including the role → privilege lookup used at sign-in.
 * Implemented by {@link PrivilegeServiceImpl}.
 */
public interface PrivilegeService {

	/**
	 * @param privilegeName the exact name
	 * @return {@code true} if a privilege with this name exists and isn't soft-deleted
	 */
	boolean checkDuplicateName(String privilegeName);

	/**
	 * Creates a privilege linked to existing access entries (by id).
	 *
	 * @param privilegeBo name and accesses (at least one)
	 * @return the saved privilege, or {@code null} if there are no accesses, an id doesn't exist,
	 *         or anything fails
	 */
	PrivilegeBo createPrivilege(PrivilegeBo privilegeBo);

	/**
	 * One page of privileges that aren't soft-deleted (without their accesses).
	 *
	 * @param pageIndex  zero-based page number
	 * @param pageSize   items per page
	 * @param sortOrder  {@code asc}, another value for descending by name, or {@code null} for unsorted
	 * @param searchText "name contains" filter, applied only together with {@code sortOrder}
	 * @return the page, or {@code null} on error
	 */
	Page<PrivilegeBo> listAllPrivileges(int pageIndex, int pageSize, String sortOrder, String searchText);

	/**
	 * @param privilegeId the id
	 * @return the privilege with its accesses sorted by name, or {@code null} if not found
	 */
	PrivilegeBo findIdByPrivilege(int privilegeId);

	/**
	 * Replaces the access entries of an existing privilege (the name isn't changed).
	 *
	 * @param privilegeBo the id and the new accesses
	 * @return {@code true} if saved, {@code false} otherwise
	 */
	boolean updatePrivilege(PrivilegeBo privilegeBo);

	/**
	 * Soft-deletes a privilege.
	 *
	 * @param privilegeId the id
	 * @return {@code true} if marked deleted; {@code false} if not found or on error
	 */
	boolean deletePrivilege(int privilegeId);

	/**
	 * @return all non-deleted privileges sorted by name, or {@code null} if there are none
	 */
	Set<PrivilegeBo> listAllPrivileges();


	/**
	 * Resolves the privileges granted by the given roles, for the JWT at sign-in.
	 *
	 * @param roleNames role names of the user
	 * @return the sorted union of privilege names of the matching active, non-deleted roles;
	 *         empty if none match
	 */
    Set<String> getPrivilegeNamesByRoleNames(Set<String> roleNames);
}
