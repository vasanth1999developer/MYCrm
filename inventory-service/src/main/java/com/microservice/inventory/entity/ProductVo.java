package com.microservice.inventory.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Entity
@Table(name="product")
public class ProductVo extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;
	private String productName;
	private String productSpecification;
	private long minStocks;
	private long maxStocks;
	private long avaliablesStocks;
	private String productType;
	private String startDate;
	private String endDate;
	private boolean isDelete;
	private boolean isActive;
	//private int employeeId;
	//private String productOwner;

	
	
	
	
	
}
