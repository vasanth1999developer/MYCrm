package com.microservices.authorizationservice.service;

import java.util.Set;

import com.microservices.authorizationservice.common.CursorResponse;
import com.microservices.authorizationservice.common.PaginatedResponse;
import com.microservices.authorizationservice.entity.AccessVo;
import org.springframework.data.domain.Page;

import com.microservices.authorizationservice.model.AccessBo;

public interface AccessService {

	boolean isDuplicateAccess(String accessName);

	AccessVo createAccess(AccessBo access);
	
	AccessBo getAccessByAccessId(int accessId);
	

	boolean updateAccess(AccessBo access);
	
	boolean deleteAccess(int accessId);

	Set<AccessBo> listAllAccesses();

	CursorResponse<AccessBo> listAccesses(Integer lastId, int size, String searchText);

	PaginatedResponse<AccessBo>  getAccess (int page,int size,String searchText,String sortBy,String direction);


}
