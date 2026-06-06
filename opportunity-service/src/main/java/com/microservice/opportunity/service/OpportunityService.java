package com.microservice.opportunity.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.microservice.opportunity.bo.OpportunityBo;
import com.microservice.opportunity.models.entity.OpportunityVo;

public interface OpportunityService {

	List<OpportunityBo> findAll();

	boolean save(OpportunityBo opportunityBo);

	OpportunityBo findById(Long id);

	boolean deleteById(Long id);

	List<OpportunityVo> getAllOpportunity() throws Exception;

	Page<OpportunityBo> listOpportunity(int pageIndex, int pageSize, String sorter, String searchName, String searchEmailId, String columnName);

}
