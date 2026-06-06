package com.microservice.company.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.company.bean.CompanyBO;
import com.microservice.company.entity.CompanyVO;
import com.microservice.company.repository.CompanyRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class CompanyServiceImpl implements CompanyService {


	@Autowired
	private CompanyRepository companyRepository;


	@Override
	public CompanyBO createCompany(CompanyBO companyBo) {
		CompanyVO companyVo = new CompanyVO();

		try {

			BeanUtils.copyProperties(companyBo, companyVo);

			companyVo.setDelete(companyBo.isDelete());
			companyVo = companyRepository.save(companyVo);
			BeanUtils.copyProperties(companyVo, companyBo);

		} catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return companyBo;
	}

	@Override
	public List<CompanyVO> getAllCompanys() {


		List<CompanyVO> companyVO = new ArrayList<>();

		try {

			companyVO = companyRepository.companyFindAll();

		} catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return companyVO;
	}

	@Override
	public CompanyBO getCompanyById(Long companyId) {
		CompanyBO companyBO=new CompanyBO();
		CompanyVO CompanyVO=new CompanyVO();

		try {

			CompanyVO=companyRepository.getById( companyId);
			BeanUtils.copyProperties(CompanyVO, companyBO);

		}catch (Exception e) {
			e.printStackTrace();

			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return companyBO;
	}

	@Override
	public CompanyBO updateCompany(CompanyBO companyBo) {
		CompanyVO companyVo = new CompanyVO();

		try {

			BeanUtils.copyProperties(companyBo, companyVo);

			companyVo.setDelete(companyBo.isDelete());
			companyVo = companyRepository.save(companyVo);
			companyBo.setCompanyId(companyVo.getCompanyId());
			BeanUtils.copyProperties(companyVo, companyBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return companyBo;
	}

	@Override
	public boolean deleteCompany(Long companyId) {
		int number;

		try {

			number	= companyRepository.companyDeleteById(companyId);
			if(number>0) {
				return true;
			}


		}catch(Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return false;
	}

	



}
