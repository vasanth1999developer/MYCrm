package com.microservices.account.model;

import lombok.Data;

@Data
public class AccountBO extends BaseBO {

	private long sectionNo;
	private int accountId;
	private String accountOwner;
	private String accountName;
	private String parentAccount;
	private String salutation;
	private String annualRevenue;
	private String 	 assigntedTo;
   private int employeeId;
	private String email;
	private long contactNumber;
	private String type;
	private String industry;
	private long noOfEmploye;
	private String street;
	private String city;
	private String state;
	private String country;
	private long pincode;
	private String description;
	private boolean isdelete;
	
}
