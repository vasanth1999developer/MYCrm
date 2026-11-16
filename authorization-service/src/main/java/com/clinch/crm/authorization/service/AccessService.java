package com.clinch.crm.authorization.service;

import java.util.Set;

import com.clinch.crm.authorization.common.CursorResponse;
import com.clinch.crm.authorization.common.PaginatedResponse;
import com.clinch.crm.authorization.entity.AccessVo;
import org.springframework.data.domain.Page;

import com.clinch.crm.authorization.model.AccessBo;

/**
 * Business logic for access entries. Implemented by {@link AccessServiceImpl}.
 */
public interface AccessService {

	/**
	 * Unused by the controllers.
	 *
	 * @param accessName the exact name
	 * @return always {@code false}; a duplicate is reported by throwing instead
	 * @throws com.clinch.crm.authorization.customexception.DuplicateResourceException if
	 *         a non-deleted entry with this name exists
	 */
	boolean isDuplicateAccess(String accessName);

	/**
	 * Saves a new access entry.
	 *
	 * @param access the entry; its name must be unique among non-deleted entries
	 * @return the saved entity, with its generated id
	 * @throws IllegalArgumentException if {@code access} is {@code null}
	 * @throws com.clinch.crm.authorization.customexception.DuplicateResourceException if
	 *         the name is taken
	 */
	AccessVo createAccess(AccessBo access);

	/**
	 * @param accessId the id
	 * @return the entry, or {@code null} if not found
	 */
	AccessBo getAccessByAccessId(int accessId);


	/**
	 * Renames an access entry.
	 *
	 * @param access the id and the new name
	 * @return {@code true} if saved; {@code false} for a missing or non-positive id
	 * @throws com.clinch.crm.authorization.customexception.ResourceNotFoundException if
	 *         no entry has this id
	 */
	boolean updateAccess(AccessBo access);

	/**
	 * Soft-deletes an access entry.
	 *
	 * @param accessId the id
	 * @return {@code true} if marked deleted; {@code false} if not found or on error
	 */
	boolean deleteAccess(int accessId);

	/**
	 * @return all non-deleted entries sorted by name, or {@code null} if there are none
	 */
	Set<AccessBo> listAllAccesses();

	/**
	 * Cursor ("keyset") paging, an alternative to page numbers. Not exposed by any endpoint yet.
	 *
	 * @param lastId     the last id of the previous page, or {@code null} for the first page
	 * @param size       items per page, capped at 50
	 * @param searchText optional case-insensitive "name contains" filter
	 * @return the items and the cursor for the next page
	 */
	CursorResponse<AccessBo> listAccesses(Integer lastId, int size, String searchText);

	/**
	 * Page-number paging used by {@code GET /authorization/access/list}.
	 *
	 * @param page       zero-based page number
	 * @param size       items per page
	 * @param searchText optional case-insensitive "name contains" filter
	 * @param sortBy     {@code accessId} or {@code accessName}
	 * @param direction  {@code desc} or ascending
	 * @return the page with its metadata
	 */
	PaginatedResponse<AccessBo>  getAccess (int page,int size,String searchText,String sortBy,String direction);


}
