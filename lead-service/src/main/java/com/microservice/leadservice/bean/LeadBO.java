package com.microservice.leadservice.bean;

import java.util.Date;

import com.microservice.leadservice.models.entity.LeadVO;

import lombok.Data;

@Data
public class LeadBO {
	 private long leadId;
   
     private long employeeId;
     private String employeeName;
     private long productId;
     private String productName;
     private boolean isDelete;
     private String status;
     private int leadSourceId;
     private String leadSourse;
     private int noOfEmployee;
     private Date created;
     private Date modified;
     private long createdBy;
     private long modifiedBy;
     private String email;
     private String leadType;

     private String salutation;
     private String firstName;
     private String lastName;
     private String designation;
     
     private String leadOwner;
     private long mobileNo;
     private String street;
     private String city;
     private String state;
     private int postalCode;
     private String country;
     
     private int annualRevenue;
     private String companyName;
     private String industry;
     private String website;
     private String rating;
     private String descriptions;
}
