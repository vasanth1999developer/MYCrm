package com.microservices.authorizationservice.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.AccessVo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccessRepository extends JpaRepository<AccessVo, Integer>, JpaSpecificationExecutor<AccessVo> {

	boolean  findByAccessNameAndIsDeleted (String accessName,boolean isDeleted);

	Page<AccessVo> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
	
	Page<AccessVo> findAllByAccessNameContainingAndIsDeleted(String searchtext, boolean isDeleted, Pageable pageable);
	
	Set<AccessVo> findAllByIsDeleted(boolean isDeleted);

	boolean existsByAccessNameAndIsDeleted(String accessName, boolean b);

	@Query("SELECT a FROM AccessVo a WHERE " +
			"(:cursor IS NULL OR a.accessId > :cursor) AND " +
			"(:searchText IS NULL OR LOWER(a.accessName) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
			"a.isDeleted = false")
	Slice<AccessVo> findAccessesWithCursor(
			@Param("cursor")     Integer cursor,
			@Param("searchText") String searchText,
			Pageable pageable);

	boolean existsByAccessIdAndIsDeleted(int accessId, boolean b);

}
