package com.microservice.opportunity.bo;

import java.util.Date;

import lombok.Data;

@Data
public class OpportunityBo {

	private long opportunityId;
	private String firstName;
	private String lastName;		
	private boolean isDelete;
	private String salutation;
	private double amount;
	private String salesStage;
	private double probability;
	private String description;
	private boolean isActive;
	private String emailAddress;
	private long createdBy;
	private long modifiedBy;	    
	private Date createdTime;
	private Date modifiedTime;
	private int accountId;
	private String accountName;
	private int leadCampId;
	private String leadSource;
	private int assignEmpId;
	private String assignto;
	private int productId;
	private String productName;
	private Date expectedClosingDate;
	private String nextStep;
	private String opportunityType;
	private long mobileNumber;
	private String street;
	private String city;
	private String state;
	private String country;
	private int postalCode;
	private String companyName;
}
