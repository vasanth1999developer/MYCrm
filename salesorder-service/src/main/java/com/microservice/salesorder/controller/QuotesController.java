package com.microservice.salesorder.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.microservice.salesorder.been.QuotesBO;
import com.microservice.salesorder.service.QuotesService;
//@CrossOrigin(origins="*")
@RestController
@RequestMapping("/quotes")
public class QuotesController {

	@Autowired
	private QuotesService quotesService;
	
	
	@PostMapping("/create-quotes")
	public ResponseEntity<?> createQuotes(@RequestBody QuotesBO quotesBo ,HttpServletRequest request){
		
		try {
			quotesBo = quotesService.createQuotes(quotesBo);	
			
			if(quotesBo!=null ) {
				
				return ResponseEntity.ok(quotesBo);
				
				
			}
			
		}
		catch (Exception e) {
			return  (ResponseEntity<?>) ResponseEntity.badRequest();
		}
		
		return  (ResponseEntity<?>) ResponseEntity.badRequest();

	}
	
	@PutMapping("/update-quotes")
	public ResponseEntity<?> updateQuotes(@RequestBody QuotesBO quotesBo ,HttpServletRequest request ){
		
		
		try {
			
			quotesBo=quotesService.updateQuotes(quotesBo);
			return ResponseEntity.ok(quotesBo);
			
		}
		catch(Exception e) {
			
			return  (ResponseEntity<?>) ResponseEntity.badRequest();
		}
		
	
	}
	
	
	
	
	
	@GetMapping("/getAllQuotes")
	public ResponseEntity<?> getAllQuotes(@RequestParam("page") int page, @RequestParam("size")int size){
		
		Page<QuotesBO> quotesList = null;
		
		try {
			quotesList=quotesService.getAllQuotes(page,size);
			return new ResponseEntity<Page<QuotesBO>>(quotesList,HttpStatus.OK);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		return  (ResponseEntity<?>) ResponseEntity.badRequest();
		}
	
	
	@GetMapping("/getQuotes/{id}")
	public ResponseEntity<?> getQuotes(@PathVariable("id") long quotesId){
		QuotesBO  quotesBo = new QuotesBO();
		try {
			quotesBo = quotesService.getQuotes(quotesId);
			return ResponseEntity.ok(quotesBo);
			
			}
		catch(Exception e) {
			return  (ResponseEntity<?>) ResponseEntity.badRequest();
		}
		}
	
	
	@DeleteMapping("/deleteQuotes/{id}")
	public ResponseEntity<?> deleteQuotes(@PathVariable("id") long quotesId){
		QuotesBO  quotesBo = new QuotesBO();
		try {
			int updatecount=quotesService.deleteQuotes(quotesId);
			
			
			return ResponseEntity.ok(updatecount);
			
		}
		catch(Exception e) {
			return  (ResponseEntity<?>) ResponseEntity.badRequest();
			
		}	
	}
		
}
	 
	
	
	
