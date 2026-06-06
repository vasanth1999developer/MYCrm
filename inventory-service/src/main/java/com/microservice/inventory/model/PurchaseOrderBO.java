package com.microservice.inventory.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class PurchaseOrderBO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long purchaseOrderId;
	private String subject;
	private String salesOrderName;
	private long customerNo;
	private Date purchaseOrderDate;
	private Date dueDate;
	private float salesCommission;
	private String accountName;
	private String status;
	private String assignedTo;
	private String billingAddress;
	private String shippingAddress;
	private String productName;
	private int quantity;
	private float unitPrice;
	private float listPrice;
	private float total;
	private String termsAndConditions;
	private String description;
	private boolean isDelete;
	private boolean isActive;	
	private long createdBy;
	private Date created = new Date();
	private Date modified = new Date();
	private long modifiedBy;

}
