package com.microservice.opportunity.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.microservice.opportunity.models.entity.OpportunityVo;

public interface OpportunityDao {

	List<OpportunityVo> findAll();

	boolean save(OpportunityVo opportunityVo);

	OpportunityVo findById(Long id);

	boolean deleteById(Long id);

	Page<OpportunityVo> searchNameList(String searchName, Pageable pageable);

	Page<OpportunityVo> searchEmailIdList(String searchEmailId, Pageable pageable);

	Page<OpportunityVo> findAllList(Pageable pageable);

}
