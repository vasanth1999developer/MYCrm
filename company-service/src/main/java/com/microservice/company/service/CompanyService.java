package com.microservice.company.service;

import java.util.List;

import com.microservice.company.bean.CompanyBO;
import com.microservice.company.entity.CompanyVO;

public interface CompanyService {

	CompanyBO createCompany(CompanyBO companyBo);

	List<CompanyVO> getAllCompanys();

	CompanyBO getCompanyById(Long companyId);

	CompanyBO updateCompany(CompanyBO companyBo);

	boolean deleteCompany(Long companyId);

}
