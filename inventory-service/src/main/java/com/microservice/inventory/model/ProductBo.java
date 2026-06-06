package com.microservice.inventory.model;



import lombok.Data;

@Data
public class ProductBo extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	

	
	
	
	
	
	
}
