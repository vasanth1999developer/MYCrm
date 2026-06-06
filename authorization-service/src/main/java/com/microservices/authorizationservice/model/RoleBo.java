package com.microservices.authorizationservice.model;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RoleBo {

	private int roleId ;
	
	@NotNull(message = "Role name must not be null")
    @NotEmpty(message = "Role name must not be empty")
	private String roleName ;
	
	private boolean isActive ;
	
	private Set<PrivilegeBo> privileges;
	
}
