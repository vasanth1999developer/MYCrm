package com.clinch.crm.company.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

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
		/** Accepted on create/update, never included in responses. */
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private String password;
		private String industryType;
		private String address;
		private String city;
		private String state;
		private String country;
		private String postalCode;
	}


