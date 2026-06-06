package com.microservice.inventory.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PriceBookBO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String priceBookOwner;
	private int priceBookId;
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
