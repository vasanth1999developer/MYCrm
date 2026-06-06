package com.microservice.salesorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.salesorder.service.InvoiceService;
import com.microservice.salesorder.service.QuotesService;
import com.microservice.salesorder.service.SalesOrderService;

@RestController
@RequestMapping("/salesorder")
public class SalesOrderController {
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private QuotesService quotesService;
	
	@Autowired 
	private InvoiceService invoiceService;
	
	@GetMapping("/listSalesorder")
	public ResponseEntity<?> salesOrderList(){
		try {
			
		} finally {
			System.out.println("SalesOrder");
		}
		return  ResponseEntity.ok("hello list");
	}
	
	@PostMapping("/createSalesorder")
	public ResponseEntity<?> salesOrderCreate(){
	
		return ResponseEntity.ok("hello create");
		
	}
	
	@PutMapping("/updateSalesorder")
	public ResponseEntity<?> salesOrderUpdate(){
	
		return ResponseEntity.ok("hello update");
		
	}
	
	@DeleteMapping("/deleteSalesorder")
	public ResponseEntity<?> salesorderDelete(){
		return ResponseEntity.ok("hello delete");
		
		
		
	}
	

}
