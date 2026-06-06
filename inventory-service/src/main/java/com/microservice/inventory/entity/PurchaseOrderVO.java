package com.microservice.inventory.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;




import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
@Table(name="purchase_order_table")
public class PurchaseOrderVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
 	private long modifiedBy;
	private Date created;
	private Date modified;
	

}
