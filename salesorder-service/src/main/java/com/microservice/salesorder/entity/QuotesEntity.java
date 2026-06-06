package com.microservice.salesorder.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Quotes_table")
public class QuotesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long quotesId;
	
	
	  @Column(unique = true)
	 private long quotesUniNumber;
	
	private String   accountName  ;
	
	
	private String pricebookName;
	
	
	
	
	
	
	
	private String   assigntedTo  ;
	
	private String   salutation  ;
	
	
	private String   email   ;
	
	private String   street  ;
	
	private String   city  ;
	
	private String   state  ;
	
	private String   country  ;
	
	private String    productTypeName ;
	
	private String    productName ;
	
	private long number;
	
	private int pincode;
	
	private long finalPrice;
	
	private String createdBy;
	
	
    private long orderedStocks;
	
	private long taxes;
	
	private long discount;
	
	private long total;
	
	
    
	
	
	
	private String modifiedBy;
	
	private String modifiedOn;
	
	private String createdOn;
	
	
	private boolean isDelete;
	
	
	
	
	
	

	public boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getPricebookName() {
		return pricebookName;
	}

	public void setPricebookName(String pricebookName) {
		this.pricebookName = pricebookName;
	}

	public long getOrderedStocks() {
		return orderedStocks;
	}

	public void setOrderedStocks(long orderedStocks) {
		this.orderedStocks = orderedStocks;
	}

	public long getTaxes() {
		return taxes;
	}

	public void setTaxes(long taxes) {
		this.taxes = taxes;
	}

	public long getDiscount() {
		return discount;
	}

	public void setDiscount(long discount) {
		this.discount = discount;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getQuotesId() {
		return quotesId;
	}

	public void setQuotesId(long quotesId) {
		this.quotesId = quotesId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	

	public String getAssigntedTo() {
		return assigntedTo;
	}

	public void setAssigntedTo(String assigntedTo) {
		this.assigntedTo = assigntedTo;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public long getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(long finalPrice) {
		this.finalPrice = finalPrice;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getQuotesUniNumber() {
		return quotesUniNumber;
	}

	public void setQuotesUniNumber(long quotesUniNumber) {
		this.quotesUniNumber = quotesUniNumber;
	}

	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
