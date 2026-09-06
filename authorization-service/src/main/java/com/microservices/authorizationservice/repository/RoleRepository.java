package com.microservices.authorizationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.RoleVo;

public interface RoleRepository extends JpaRepository<RoleVo, Integer> {

	RoleVo findByRoleNameAndIsDeleted(String roleName, boolean isDeleted);

	Page<RoleVo> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
	
	Page<RoleVo> findAllByRoleNameContainingIgnoreCaseAndIsDeleted(String searchtext, boolean isDeleted, Pageable pageable);

    boolean existsByRoleName(String roleName);
}
