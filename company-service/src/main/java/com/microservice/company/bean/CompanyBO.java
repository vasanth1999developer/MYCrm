package com.microservice.company.bean;

import java.util.Date;

import lombok.Data;

@Data
public class CompanyBO {
	


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


