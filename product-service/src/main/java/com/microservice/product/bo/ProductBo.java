package com.microservice.product.bo;

public class ProductBo extends BaseBo {

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

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductSpecification() {
		return productSpecification;
	}

	public void setProductSpecification(String productSpecification) {
		this.productSpecification = productSpecification;
	}

	public long getMinStocks() {
		return minStocks;
	}

	public void setMinStocks(long minStocks) {
		this.minStocks = minStocks;
	}

	public long getMaxStocks() {
		return maxStocks;
	}

	public void setMaxStocks(long maxStocks) {
		this.maxStocks = maxStocks;
	}

	public long getAvaliablesStocks() {
		return avaliablesStocks;
	}

	public void setAvaliablesStocks(long avaliablesStocks) {
		this.avaliablesStocks = avaliablesStocks;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
