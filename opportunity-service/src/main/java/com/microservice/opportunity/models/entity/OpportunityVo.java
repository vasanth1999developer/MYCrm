package com.microservice.opportunity.models.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="opportunity_table")
public class OpportunityVo{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	 @Column(nullable = false, unique = true)
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
	
	@Column(name="opportunity_type")
	private String opportunityType;
	
	@Column(name="mobile_number")
	private long mobileNumber;
	
	@Column(name="street")
	private String street;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="country")
	private String country;
	
	@Column(name="postal_code")
	private int postalCode;
	
	private String companyName;
}
