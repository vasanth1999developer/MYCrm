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
@Table(name="pricebook")
public class PriceBookVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int priceBookId;
	private String priceBookOwner;
	private String priceBookName;
	private int assignEmpId;
	private int productId;
	private String productName;
	private String supplierName;
	private double specifyPrice;
	private double finalPrice;
	private String description;
	private boolean isDelete;
	private boolean isActive=true;
	private int createdBy;
 	private int modifyiedBy;
	private Date CreatedTime;
	private Date ModifiedTime;
}