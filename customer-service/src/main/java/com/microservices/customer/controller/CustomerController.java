package com.microservices.customer.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.customer.entity.CustomerVo;
import com.microservices.customer.model.CustomerBo;
import com.microservices.customer.service.CustomerService;

import lombok.extern.log4j.Log4j2;

@Validated
@RestController
@RequestMapping("customer")
@Log4j2
@Endpoint(id="customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping(value="/v1/create-customer")
	public ResponseEntity<?> createCustomer(@RequestBody CustomerBo CustomerBo, BindingResult resulte,
			HttpServletRequest request
			) throws Exception {
		try {
			if (resulte.hasErrors()) {

			}
			if(null!=request.getHeader("id")) {
				String id=request.getHeader("id");
				Long number = Long.parseLong(id);
				CustomerBo.setCreatedBy(number);
				CustomerBo.setCreatedTime(new Date());
				CustomerBo.setDelete(false);
			}
			CustomerBo = customerService.createCustomer(CustomerBo);
			if (CustomerBo.getCustomerId() > 0) {
				return ResponseEntity.ok(CustomerBo);
			}
		} catch (Exception ex) {
			if(log.isDebugEnabled()) {
				log.info(ex.getMessage(),ex);
			}
		}
		return ResponseEntity.ok(CustomerBo);

	}
	@ReadOperation
	@GetMapping("/v1/view-customer")
	public ResponseEntity<?> viewProject() throws Exception {
		CustomerBo CustomerBo= new CustomerBo();
		List<CustomerBo> CustomerBoList = new ArrayList<CustomerBo>();
		try {
			CustomerBo.setDelete(false);
			CustomerBoList = customerService.viewCustomer(CustomerBo);
			if (null != CustomerBoList && !CustomerBoList.isEmpty() && CustomerBoList.size() > 0) {
				return ResponseEntity.ok(CustomerBoList);
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;
	}
	@GetMapping("/v1/edit-customer/{id}")
	public ResponseEntity<?> editProject(@PathVariable int id) throws Exception {
		CustomerBo CustomerBo= new CustomerBo();
		try {

			CustomerBo.setCustomerId(id);
			CustomerBo = customerService.retrieveCustomerById(CustomerBo);
			if (null != CustomerBo) {
				return ResponseEntity.ok(CustomerBo);
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;
	}

	@PutMapping("/v1/update-customer")
	public ResponseEntity<?> updateCustomer(@Valid @RequestBody CustomerBo CustomerBo, BindingResult result,
			HttpServletRequest request ) throws Exception {
		try {
			if (result.hasErrors()) {

			}
			if(null!=request.getHeader("id")) {
				String id=request.getHeader("id");
				Long number = Long.parseLong(id);
				CustomerBo.setModifiedBy(number);
				CustomerBo.setModifiedTime(new Date());
			}
			boolean CustomerB = customerService.updateCustomer(CustomerBo);
			return ResponseEntity.ok(CustomerB);
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return ResponseEntity.ok(false);
	}

	@DeleteMapping("/v1/delete-customer/{id}")
	public  ResponseEntity<?> deleteCustomer(@PathVariable int id)throws Exception{
		try{
			CustomerBo CustomerBo= new CustomerBo();
			CustomerBo.setCustomerId(id);
			CustomerBo.setDelete(true);
			CustomerBo = customerService.deleteCustomer(CustomerBo);
		}catch(Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;
	}

	@GetMapping("/v1/checkEmailId/{emailAddress}")
	public ResponseEntity<?> checkEmailId(@PathVariable("emailAddress") String emailAddress,CustomerBo CustomerBo)throws Exception  {
		boolean status=false;	
		try {
			status=customerService.findByEmail(emailAddress);

		}catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}

		}
		return ResponseEntity.ok(status);
	}
	@GetMapping("/v1/checkMobileNo/{mobileNo}")
	public ResponseEntity<?> checkMobileNo(@PathVariable("mobileNo") String mobileNo,CustomerBo CustomerBo)throws Exception  {
		boolean status=false;	
		try {
			status=customerService.findByMobileNo(mobileNo);

		}catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}

		}
		return ResponseEntity.ok(status);
	}
	@GetMapping("/v1/pageRecord")
	public ResponseEntity<Page<CustomerVo>> getItems(@RequestParam int page, @RequestParam int size){
		Page<CustomerVo> pageRecords = null;
		try {
			pageRecords = customerService.getPageRecords(page,size);
		}catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return ResponseEntity.ok(pageRecords);
	}
}
