package com.microservices.authorizationservice.model;

import java.util.Set;

import lombok.Data;

@Data
public class PrivilegeBo {
	
	
	private int privilegeId;
	
	private String privilegeName;
	

	private Set<AccessBo> accesses;
	
	

}
