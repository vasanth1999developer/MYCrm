package com.microservices.authorizationservice.repository;


import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.authorizationservice.entity.PrivilegeVo;

public interface PrivilegeRepository extends JpaRepository<PrivilegeVo,Integer> {

	PrivilegeVo findByPrivilegeNameAndIsDelete(String privilegeName, boolean b);

	Page<PrivilegeVo> findAllByPrivilegeNameContainingIgnoreCaseAndIsDelete(String searchText, boolean b, Pageable pageable);

	Page<PrivilegeVo> findAllByIsDelete(boolean b, Pageable pageable);

	Set<PrivilegeVo> findAllByIsDelete(boolean isDelete);
	
	
	
	
	
	

}
