package com.microservice.inventory.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SupplierBO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long supplierId;
	private String supplierName;
	private String emailId;
	private Long mobileNo;
	private String address;
	private String city;
	private String state;
	private String country;
	private String website;
	private String techOriented;
	private double financialAmount;
	private double rating;
	private String location;
	private boolean isActive;
	private boolean isDelete;
	private long createdBy;
	private Date created = new Date();
	private Date modified = new Date();
	private long modifiedBy;
	


}
