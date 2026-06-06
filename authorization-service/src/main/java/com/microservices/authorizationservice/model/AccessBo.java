package com.microservices.authorizationservice.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AccessBo {


	private int accessId ;
	
	@NotNull(message = "Access name must not be null")
    @NotEmpty(message = "Access name must not be empty")
	private String accessName ;


	public AccessBo(int accessId, String accessName) {
		this.accessId=accessId;
		this.accessName=accessName;
	}

	public AccessBo() {

	}
}
