package com.microservice.leadservice.models.entity;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Data
@Table(name="leadTable")

public class LeadVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long leadId;
    private long employeeId;
    private String employeeName;
    private long productId;
    private String productName;
    private boolean isDelete;
    private boolean status;
    private String leadSourse;
    private int noOfEmployee;
    private Date created;
    private Date modified;
    private long createdBy;
    private long modifiedBy;
    private String email;
     private String salutation;
     private String firstName;
     private String lastName;
     private String designation;
     private String leadType;

     private String leadOwner;
     private long mobileNo;
     private String street;
     private String city;
     private String state;
     private int postalCode;
     private String country;
     private int leadSourceId;

     
     private int annualRevenue;
     private String companyName;
     private String industry;
     private String website;
     private String rating;
     private String descriptions;
}
