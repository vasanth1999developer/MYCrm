package com.clinch.crm.authorization.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.clinch.crm.authorization.entity.AccessVo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository for {@link AccessVo}.
 *
 * <p>Spring generates the implementation. Methods named {@code findBy…}/{@code existsBy…} are
 * <i>derived queries</i> built from the method name. {@link JpaSpecificationExecutor} adds
 * {@code findAll(Specification, Pageable)}, used with {@code AccessSpecification}.
 */
public interface AccessRepository extends JpaRepository<AccessVo, Integer>, JpaSpecificationExecutor<AccessVo> {

	/**
	 * Unused. A {@code find} query returning {@code boolean} doesn't fit Spring Data's
	 * conventions; use {@link #existsByAccessNameAndIsDeleted} instead.
	 *
	 * @param accessName the name
	 * @param isDeleted  the soft-delete flag
	 * @return not meaningful
	 */
	boolean  findByAccessNameAndIsDeleted (String accessName,boolean isDeleted);

	/**
	 * Unused.
	 *
	 * @param isDeleted the soft-delete flag to match
	 * @param pageable  page, size and sort
	 * @return one page of entries
	 */
	Page<AccessVo> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

	/**
	 * Unused (case-sensitive "contains" search).
	 *
	 * @param searchtext text the name must contain
	 * @param isDeleted  the soft-delete flag to match
	 * @param pageable   page, size and sort
	 * @return one page of entries
	 */
	Page<AccessVo> findAllByAccessNameContainingAndIsDeleted(String searchtext, boolean isDeleted, Pageable pageable);

	/**
	 * Used by {@code list-all}.
	 *
	 * @param isDeleted the soft-delete flag to match ({@code false} for live entries)
	 * @return all matching entries
	 */
	Set<AccessVo> findAllByIsDeleted(boolean isDeleted);

	/**
	 * Duplicate-name check used on create.
	 *
	 * @param accessName the exact name
	 * @param b          the soft-delete flag to match ({@code false} for live entries)
	 * @return {@code true} if a matching entry exists
	 */
	boolean existsByAccessNameAndIsDeleted(String accessName, boolean b);

	/**
	 * Cursor paging: live entries with an id above {@code cursor}, optionally filtered by name.
	 *
	 * <p>JPQL (queries entity and field names, not tables). {@code :cursor IS NULL} lets the same
	 * query serve the first page. Returns a {@link Slice}, which knows whether there's a next page
	 * without running a {@code COUNT} query.
	 *
	 * @param cursor     last id of the previous page, or {@code null} for the first page
	 * @param searchText case-insensitive "name contains" filter, or {@code null}
	 * @param pageable   the page size (page number should be 0); add a sort by {@code accessId}
	 *                   for a stable order
	 * @return the next items
	 */
	@Query("SELECT a FROM AccessVo a WHERE " +
			"(:cursor IS NULL OR a.accessId > :cursor) AND " +
			"(:searchText IS NULL OR LOWER(a.accessName) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
			"a.isDeleted = false")
	Slice<AccessVo> findAccessesWithCursor(
			@Param("cursor")     Integer cursor,
			@Param("searchText") String searchText,
			Pageable pageable);

	/**
	 * Unused.
	 *
	 * @param accessId the id
	 * @param b        the soft-delete flag to match
	 * @return {@code true} if a matching entry exists
	 */
	boolean existsByAccessIdAndIsDeleted(int accessId, boolean b);

}
