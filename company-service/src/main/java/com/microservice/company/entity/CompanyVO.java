package com.microservice.company.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="campany")
public class CompanyVO {

	
	
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long companyId;
		private String companyName;
		private String contactPerson;
		private String companyGSTNo;
		private String companyEmail;
		private Long contactNo;
		private Long mobileNo;
		private boolean isDelete;
		private boolean isActiveStatus;
		private Date created;
		private Date modified;
		private long createdBy;
		private long modifiedBy;
		private String companyWebsite;
		private String password;
		private String industryType;
		private String address;
		private String city;
		private String state;
		private String country;
		private String postalCode;

	}

