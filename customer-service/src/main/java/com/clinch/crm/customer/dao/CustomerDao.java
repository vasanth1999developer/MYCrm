package com.clinch.crm.customer.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.clinch.crm.customer.entity.CustomerVo;
import com.clinch.crm.customer.model.CustomerBo;

public interface CustomerDao {
	long createCustomer(CustomerVo CustomerVo)throws Exception;

	List<CustomerVo> viewCustomer(CustomerVo CustomerVo)throws Exception;
	CustomerVo retrieveCustomerById(CustomerVo CustomerVo)throws Exception;

	boolean updateCustomer(CustomerVo CustomerVo)throws Exception;

	CustomerVo deleteCustomer(CustomerVo CustomerVo)throws Exception;  
	boolean findByParam(String string, String emailAddress)throws Exception;
	boolean findByMobileNo(String string, String emailAddress)throws Exception;

	Page<CustomerVo>findPaginated(PageRequest pageable);

	
}
