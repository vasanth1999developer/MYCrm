package com.microservices.authorizationservice.repository;


import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.PrivilegeVo;

/**
 * Spring Data repository for {@link PrivilegeVo}. All methods are derived queries built from the
 * method name; the boolean parameter matches the {@code isDelete} flag.
 */
public interface PrivilegeRepository extends JpaRepository<PrivilegeVo,Integer> {

	/**
	 * Duplicate-name check.
	 *
	 * @param privilegeName the exact name
	 * @param b             the soft-delete flag to match ({@code false} for live privileges)
	 * @return the privilege, or {@code null}; throws if several match
	 */
	PrivilegeVo findByPrivilegeNameAndIsDelete(String privilegeName, boolean b);

	/**
	 * Case-insensitive "name contains" search, paged.
	 *
	 * @param searchText text the name must contain
	 * @param b          the soft-delete flag to match
	 * @param pageable   page, size and sort
	 * @return one page of privileges
	 */
	Page<PrivilegeVo> findAllByPrivilegeNameContainingIgnoreCaseAndIsDelete(String searchText, boolean b, Pageable pageable);

	/**
	 * All privileges with the given flag, paged.
	 *
	 * @param b        the soft-delete flag to match
	 * @param pageable page, size and sort
	 * @return one page of privileges
	 */
	Page<PrivilegeVo> findAllByIsDelete(boolean b, Pageable pageable);

	/**
	 * All privileges with the given flag, unpaged (for {@code list-all}).
	 *
	 * @param isDelete the soft-delete flag to match
	 * @return all matching privileges
	 */
	Set<PrivilegeVo> findAllByIsDelete(boolean isDelete);







}
