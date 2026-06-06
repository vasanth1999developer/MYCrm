package com.microservices.authorizationservice.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.model.PrivilegeBo;

public interface PrivilegeService {

	boolean checkDuplicateName(String privilegeName);

	PrivilegeBo createPrivilege(PrivilegeBo privilegeBo);

	Page<PrivilegeBo> listAllPrivileges(int pageIndex, int pageSize, String sortOrder, String searchText);

	PrivilegeBo findIdByPrivilege(int privilegeId);

	boolean updatePrivilege(PrivilegeBo privilegeBo);

	boolean deletePrivilege(int privilegeId);
	
	Set<PrivilegeBo> listAllPrivileges();
	
	
	
	
	
	
	
	
	
	

}
