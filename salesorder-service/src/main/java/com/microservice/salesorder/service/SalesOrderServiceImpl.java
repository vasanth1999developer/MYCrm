package com.microservice.salesorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.salesorder.repository.SalesOrderDao;

@Service
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService{
	
	@Autowired
	private SalesOrderDao salesOrderDao;

}
