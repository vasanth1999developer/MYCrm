package com.microservices.customer.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


 

@Entity
@Table(name = "customer")
public class CustomerVo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerId;
	@Column(name="assignEmployee")
	private String assignEmployee;
	@Column(name="firstName")
	private String firstName;
	@Column(name="lastName")
	private String lastName;
	@Column(name="emailId")
	private String emailId;
	@Column(name="contactNo")
	private String contactNo;
	@Column(name="mobileNo")
	private String mobileNo;
	@Column(name="primaryMobileNo")
	private String primaryMobileNo;
	public String getPrimaryMobileNo() {
		return primaryMobileNo;
	}

	public void setPrimaryMobileNo(String primaryMobileNo) {
		this.primaryMobileNo = primaryMobileNo;
	}

	@Column(name="company")
	private String company;
	@Column(name="industry")
	private String industry;
	@Column(name="webSite")
	private String webSite;
	@Column(name="address")
	private String address;
	@Column(name="warrantyDate")
	private String warrantyDate;
	@Column(name="productName")
	private String productName;

	private boolean isDelete;
	@Column(name="createdBy")
private Long createdBy;
	@Column(name="modifiedBy")
	private Long modifiedBy;
	@Column(name="createdTime")
	private Date createdTime;
	@Column(name="modifiedTime")
	private Date modifiedTime;
	@Column(name="gender")
	private String gender;
	
	
	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getAssignEmployee() {
		return assignEmployee;
	}

	public void setAssignEmployee(String assignEmployee) {
		this.assignEmployee = assignEmployee;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	 

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWarrantyDate() {
		return warrantyDate;
	}

	public void setWarrantyDate(String warrantyDate) {
		this.warrantyDate = warrantyDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
