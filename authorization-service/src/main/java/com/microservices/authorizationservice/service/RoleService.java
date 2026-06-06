package com.microservices.authorizationservice.service;

import org.springframework.data.domain.Page;

import com.microservices.authorizationservice.model.RoleBo;

public interface RoleService {
	
	boolean isDuplicateRole(String roleName);

	boolean createRole(RoleBo role);
	
	RoleBo getRoleByRoleId(int roleId);
	
	Page<RoleBo> listRoles(int pageIndex,int pageSize,String sortOrder,String searchText);
	
	boolean updateRole(RoleBo role);
	
	boolean deleteRole(int roleId);
	
	boolean switchActivity(int roleId);
		
}
