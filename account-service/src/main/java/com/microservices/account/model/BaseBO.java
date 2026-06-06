package com.microservices.account.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BaseBO implements Serializable {
	
	private static final long serialVersionUID = -2337868016112279206L;
	
	
	private long createdBy;
    private Date created = new Date();
    private Date modified = new Date();
    private long modifiedBy;

}
