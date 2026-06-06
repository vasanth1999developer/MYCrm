package com.microservice.salesorder.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="salesorder_table")
public class SalesOrderEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long saleorderId;

	public long getSaleorderId() {
		return saleorderId;
	}

	public void setSaleorderId(long saleorderId) {
		this.saleorderId = saleorderId;
	}
	
	

}
