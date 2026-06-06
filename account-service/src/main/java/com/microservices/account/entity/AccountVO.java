package com.microservices.account.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "account")
@Data
public class AccountVO extends BaseVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accountId;
	private String accountOwner;
	private String accountName;
	private String parentAccount;
	private String salutation;
	private String annualRevenue;
	private String email;
	private long contactNumber;
	 private int employeeId;
	private String assigntedTo;
	
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
