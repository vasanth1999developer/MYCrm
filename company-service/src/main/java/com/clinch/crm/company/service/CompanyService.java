package com.clinch.crm.company.service;

import java.util.List;

import com.clinch.crm.company.bean.CompanyBO;
import com.clinch.crm.company.entity.CompanyVO;

public interface CompanyService {

	CompanyBO createCompany(CompanyBO companyBo);

	List<CompanyVO> getAllCompanys();

	CompanyBO getCompanyById(Long companyId);

	CompanyBO updateCompany(CompanyBO companyBo);

	boolean deleteCompany(Long companyId);

}
