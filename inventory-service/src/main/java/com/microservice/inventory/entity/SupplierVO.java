package com.microservice.inventory.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="supplier")
public class SupplierVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private int createdBy;
 	private int modifyiedBy;
	private Date CreatedTime;
	private Date ModifiedTime;
	//private String supplierOwner;
	

}
