package com.microservices.customer.service;

import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.customer.dao.CustomerDao;
import com.microservices.customer.entity.CustomerVo;
import com.microservices.customer.model.CustomerBo;

import lombok.extern.log4j.Log4j2;





@Service
@Transactional
@Log4j2
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDao CustomerDao;

	@Override
	public CustomerBo createCustomer(CustomerBo CustomerBo) throws Exception {
		long customerId = 0;
		try {
			CustomerVo CustomerVo = new CustomerVo();

			BeanUtils.copyProperties(CustomerBo, CustomerVo);
			customerId = CustomerDao.createCustomer(CustomerVo);
			if (customerId > 0) {
				BeanUtils.copyProperties(CustomerVo, CustomerBo);
			}
		} catch (Exception ex) {
			if(log.isDebugEnabled()) {
				log.info(ex.getMessage(),ex);
			}
		}
		return CustomerBo;
	}
	@Override
	public List<CustomerBo> viewCustomer(CustomerBo CustomerBo) throws Exception {
		List<CustomerBo> customerDaoBoList = new ArrayList<CustomerBo>();
		List<CustomerVo> customerDaoVoList = new ArrayList<CustomerVo>();
		try {
			CustomerVo CustomerVo = new CustomerVo();
			CustomerVo.setDelete(CustomerBo.isDelete());
			customerDaoVoList = CustomerDao.viewCustomer(CustomerVo);
			// int sno = 1;
			for (CustomerVo Customervo : customerDaoVoList) {
				CustomerBo customerBo= new CustomerBo();
				customerBo.setCustomerId(Customervo.getCustomerId());
				customerBo.setFirstName(Customervo.getFirstName());
				customerBo.setLastName(Customervo.getLastName());
				customerBo.setEmailId(Customervo.getEmailId());
				customerBo.setWarrantyDate(Customervo.getWarrantyDate());
				customerBo.setAddress(Customervo.getAddress());
				customerBo.setAssignEmployee(Customervo.getAssignEmployee());
				customerBo.setCompany(Customervo.getCompany());
				customerBo.setContactNo(Customervo.getContactNo());
				customerBo.setProductName(Customervo.getProductName());
				customerBo.setMobileNo(Customervo.getMobileNo());
				customerBo.setIndustry(Customervo.getIndustry());
				customerBo.setWebSite(Customervo.getWebSite());
				customerBo.setGender(Customervo.getGender());
				customerDaoBoList.add(customerBo);
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return customerDaoBoList;
	}
	@Override
	public CustomerBo retrieveCustomerById(CustomerBo CustomerBo) throws Exception {
		// TODO Auto-generated method stub
		try {
			CustomerVo CustomerVo = new CustomerVo();
			CustomerVo.setCustomerId(CustomerBo.getCustomerId());

			CustomerVo = CustomerDao.retrieveCustomerById(CustomerVo);
			if (CustomerVo != null) {
				BeanUtils.copyProperties(CustomerVo, CustomerBo);
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return CustomerBo;
	}

	@Override
	public boolean updateCustomer(CustomerBo CustomerBo) throws Exception {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			CustomerVo CustomerVo = new CustomerVo();
			CustomerVo.setCustomerId(CustomerBo.getCustomerId());	

			CustomerVo = CustomerDao.retrieveCustomerById(CustomerVo);
			if (null != CustomerVo) {

				CustomerVo.setCustomerId(CustomerBo.getCustomerId());
				CustomerVo.setFirstName(CustomerBo.getFirstName());
				CustomerVo.setLastName(CustomerBo.getLastName());
				CustomerVo.setEmailId(CustomerBo.getEmailId());
				CustomerVo.setWarrantyDate(CustomerBo.getWarrantyDate());
				CustomerVo.setAddress(CustomerBo.getAddress());
				CustomerVo.setAssignEmployee(CustomerBo.getAssignEmployee());
				CustomerVo.setCompany(CustomerBo.getCompany());
				CustomerVo.setContactNo(CustomerBo.getContactNo());
				CustomerVo.setProductName(CustomerBo.getProductName());
				CustomerVo.setMobileNo(CustomerBo.getMobileNo());
				CustomerVo.setIndustry(CustomerBo.getIndustry());
				CustomerVo.setWebSite(CustomerBo.getWebSite());
				CustomerVo.setModifiedBy(CustomerBo.getModifiedBy());
				CustomerVo.setModifiedTime(CustomerBo.getModifiedTime());
				status = CustomerDao.updateCustomer(CustomerVo);

			}

		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return status;
	}

	@Override
	public CustomerBo deleteCustomer(CustomerBo CustomerBo) throws Exception {
		try {
			CustomerVo CustomerVo = new CustomerVo();
			CustomerVo.setCustomerId(CustomerBo.getCustomerId());
			CustomerVo.setDelete(CustomerBo.isDelete());
			CustomerVo = CustomerDao.deleteCustomer(CustomerVo);
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;
	}
	@Override
	public boolean findByEmail(String emailAddress) throws Exception {
		boolean isEmail = false;
		isEmail = CustomerDao.findByParam("emailAddress", emailAddress);
		return isEmail;

	}
	@Override
	public boolean findByMobileNo(String mobileno) throws Exception {
		boolean isEmail = false;
		isEmail = CustomerDao.findByMobileNo("mobileNo", mobileno);
		return isEmail;

	}
	public Page<CustomerVo> getPageRecords(int page, int size) {
		try {
			PageRequest pageable = PageRequest.of(page, size);
			return CustomerDao.findPaginated(pageable);
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;

	}


}
