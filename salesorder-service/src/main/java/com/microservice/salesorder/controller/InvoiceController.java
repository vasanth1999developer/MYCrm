package com.microservice.salesorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.salesorder.service.InvoiceService;

@RestController
@RequestMapping("/Invoice")
public class InvoiceController {

	
	@Autowired 
	private InvoiceService invoiceService;
	
	
}
