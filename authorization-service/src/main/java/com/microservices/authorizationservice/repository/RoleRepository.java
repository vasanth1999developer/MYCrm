package com.microservices.authorizationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.RoleVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Spring Data repository for {@link RoleVo}. Derived queries plus one hand-written JPQL query
 * used at sign-in.
 */
public interface RoleRepository extends JpaRepository<RoleVo, Integer> {

	/**
	 * Duplicate-name check.
	 *
	 * @param roleName  the exact name
	 * @param isDeleted the soft-delete flag to match ({@code false} for live roles)
	 * @return the role, or {@code null}; throws if several match
	 */
	RoleVo findByRoleNameAndIsDeleted(String roleName, boolean isDeleted);

	/**
	 * All roles with the given flag, paged.
	 *
	 * @param isDeleted the soft-delete flag to match
	 * @param pageable  page, size and sort
	 * @return one page of roles
	 */
	Page<RoleVo> findAllByIsDeleted(boolean isDeleted, Pageable pageable);

	/**
	 * Case-insensitive "name contains" search, paged.
	 *
	 * @param searchtext text the name must contain
	 * @param isDeleted  the soft-delete flag to match
	 * @param pageable   page, size and sort
	 * @return one page of roles
	 */
	Page<RoleVo> findAllByRoleNameContainingIgnoreCaseAndIsDeleted(String searchtext, boolean isDeleted, Pageable pageable);

	/**
	 * Used by {@code GET /authorization/role/exists}. Ignores the active and deleted flags.
	 *
	 * @param roleName the exact name
	 * @return {@code true} if any role has this name
	 */
    boolean existsByRoleName(String roleName);



	/**
	 * Sign-in lookup: the active, non-deleted roles with these names, <b>with their privileges
	 * loaded in the same query</b>.
	 *
	 * <p>{@code LEFT JOIN FETCH} loads the lazy {@code privileges} collection right away, avoiding
	 * one extra query per role (the "N+1" problem). {@code DISTINCT} removes the duplicate role
	 * rows the join produces (one per privilege). Privileges aren't filtered on their own
	 * {@code isDelete} flag.
	 *
	 * @param names role names
	 * @return the matching roles; unknown, inactive or deleted names are simply absent
	 */
		@Query("SELECT DISTINCT r FROM RoleVo r " +
				"LEFT JOIN FETCH r.privileges " +
				"WHERE r.roleName IN :names " +
				"  AND r.isDeleted = false " +
				"  AND r.isActive = true")
		List<RoleVo> findActiveByRoleNames(@Param("names") Set<String> names);

}
