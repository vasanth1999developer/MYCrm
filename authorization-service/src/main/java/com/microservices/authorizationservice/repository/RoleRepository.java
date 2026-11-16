package com.microservices.authorizationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.RoleVo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<RoleVo, Integer> {

	RoleVo findByRoleNameAndIsDeleted(String roleName, boolean isDeleted);

	Page<RoleVo> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
	
	Page<RoleVo> findAllByRoleNameContainingIgnoreCaseAndIsDeleted(String searchtext, boolean isDeleted, Pageable pageable);

    boolean existsByRoleName(String roleName);



		@Query("SELECT DISTINCT r FROM RoleVo r " +
				"LEFT JOIN FETCH r.privileges " +
				"WHERE r.roleName IN :names " +
				"  AND r.isDeleted = false " +
				"  AND r.isActive = true")
		List<RoleVo> findActiveByRoleNames(@Param("names") Set<String> names);

}
