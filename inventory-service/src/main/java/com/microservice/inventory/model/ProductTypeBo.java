package com.microservice.inventory.model;

public class ProductTypeBo extends BaseBO{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long productTypeId;
	private String productType;
	private boolean isDelete;
	private boolean isActive;
	
	
	public long getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(long productTypeId) {
		this.productTypeId = productTypeId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	

}
