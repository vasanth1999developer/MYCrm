package com.microservices.customer.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.microservices.customer.entity.CustomerVo;
import com.microservices.customer.model.CustomerBo;

public interface CustomerService {
	
	CustomerBo createCustomer(CustomerBo CustomerBo)throws Exception;
	List<CustomerBo> viewCustomer(CustomerBo CustomerBo)throws Exception;
	CustomerBo retrieveCustomerById(CustomerBo CustomerBo)throws Exception;

	boolean updateCustomer(CustomerBo CustomerBo)throws Exception;

	CustomerBo deleteCustomer(CustomerBo CustomerBo)throws Exception;
	boolean findByEmail(String emailAddress)throws Exception;
	boolean findByMobileNo(String emailAddress)throws Exception;
	Page<CustomerVo> getPageRecords(int page, int size);
	
	
}