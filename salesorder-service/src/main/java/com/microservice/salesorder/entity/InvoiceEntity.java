package com.microservice.salesorder.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Invoice_table")
public class InvoiceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long invoiceId;

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	

}
