package com.microservice.inventory.model;

import java.io.Serializable;
import java.util.Date;

public class BaseBO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2337868016112279206L;
	
	private long createdBy;
	private Date created = new Date();
	private Date modified = new Date();
	private long modifiedBy;
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public long getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	
	
}

