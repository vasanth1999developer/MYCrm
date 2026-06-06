package com.microservice.company.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.company.bean.CompanyBO;
import com.microservice.company.entity.CompanyVO;
import com.microservice.company.service.CompanyService;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/company")
@RestController
@Log4j2
public class CompanyController {
	
	@Autowired
	CompanyService companyService;
	
	
	@PostMapping("/create-company")
	public ResponseEntity<?> createCompany(@RequestBody CompanyBO companyBo,HttpServletRequest request)throws Exception{

		try {
			if(null!=request.getHeader("id")) {
				String id=request.getHeader("id");
				Long number = Long.parseLong(id);
				companyBo.setCreatedBy(number);
			}

			companyBo.setDelete(false);
			companyBo.setActiveStatus(true);
			companyBo.setCreated(new Date());
			companyBo.setModified(new Date());
			companyBo = companyService.createCompany(companyBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}

		}

		return ResponseEntity.ok(companyBo);

	}

	@GetMapping("/get-companyList")
	public ResponseEntity<?> getAllCompanys()throws Exception{

		List<CompanyVO> companyVo = new ArrayList<>();

		try {

			companyVo = companyService.getAllCompanys();

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(companyVo);

	}
	@PutMapping("/update-company")
	public ResponseEntity<?> updateCompany(@RequestBody CompanyBO companyBo ,HttpServletRequest request)throws Exception{

		CompanyBO companyBO =new CompanyBO();

		try {
             Long companyId=0L;
			if(null!=companyBo.getCompanyId()) {
				
				companyId = companyBo.getCompanyId();
				companyBo.setModifiedBy(companyId);
			}

			companyBO = companyService.getCompanyById(companyId);
			
			companyBo.setCompanyId(companyId);
			companyBo.setCreatedBy(companyBO.getCreatedBy());
			companyBo.setCreated(companyBO.getCreated());
			companyBo.setDelete(false);
			companyBo.setActiveStatus(true);
			companyBo.setModified(new Date());
			
			companyBo = companyService.updateCompany(companyBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(companyBo);

	}
	@GetMapping("/get-companyById/{id}")
	public ResponseEntity<?> getCompanyById(@PathVariable("id") Long CompanyId)throws Exception{

		CompanyBO companyBO =new CompanyBO();

		try {

			companyBO = companyService.getCompanyById(CompanyId);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(companyBO);

	}

	@DeleteMapping("/delete-company/{id}")
	public ResponseEntity<Boolean> deleteCompany(@PathVariable("id") Long companyId)throws Exception{

		boolean status = false;

		try {

			status = companyService.deleteCompany(companyId);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return ResponseEntity.ok(status);

	}




}
