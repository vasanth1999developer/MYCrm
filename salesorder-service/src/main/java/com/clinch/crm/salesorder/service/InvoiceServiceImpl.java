package com.clinch.crm.salesorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinch.crm.salesorder.repository.InvoiceDao;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService{
	
	@Autowired
	private InvoiceDao invoiceDao;

}
