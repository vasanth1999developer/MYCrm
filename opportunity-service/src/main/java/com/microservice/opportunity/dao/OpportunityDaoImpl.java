package com.microservice.opportunity.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.microservice.opportunity.models.entity.OpportunityVo;
import com.microservice.opportunity.repository.OpportunityRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class OpportunityDaoImpl implements OpportunityDao {

	@Autowired
	OpportunityRepository opportunityRepository;

	@Override
	public List<OpportunityVo> findAll() {

		List<OpportunityVo> opportunityVoList;
		try {
			opportunityVoList = opportunityRepository.findAll();
			if (null != opportunityVoList && 0 < opportunityVoList.size() && !opportunityVoList.isEmpty()) {
				return opportunityVoList;
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
			return null;
		}
		return null;
	}

	@Override
	public boolean save(OpportunityVo opportunityVo) {

		try {
			opportunityVo = opportunityRepository.save(opportunityVo);
			if (null != opportunityVo) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return false;
	}

	@Override
	public OpportunityVo findById(Long id) {

		OpportunityVo opportunityVo = null;
		try {
			Optional<OpportunityVo> opportunityVoOptional = opportunityRepository.findById(id);
			if (opportunityVoOptional.isPresent()) {
				opportunityVo = opportunityVoOptional.get();
			} else {
				return null;
			}
		} catch (Exception e) {
			// Log the exception if debug is enabled.
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return opportunityVo;
	}

	@Override
	public boolean deleteById(Long id) {

		boolean status = false;
		try {
			int update = opportunityRepository.DeleteById(id);
			if (0 < update) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return status;
	}

	@Override
	public Page<OpportunityVo> searchNameList(String searchName, Pageable pageable) {

		return opportunityRepository.findAllByFirstNameContainingAndIsDeleteFalse(searchName, pageable);
	}

	@Override
	public Page<OpportunityVo> searchEmailIdList(String searchEmailId, Pageable pageable) {

		return opportunityRepository.findAllByEmailAddressContainingAndIsDeleteFalse(searchEmailId, pageable);
	}

	@Override
	public Page<OpportunityVo> findAllList(Pageable pageable) {

		return opportunityRepository.findAllByIsDeleteFalse(pageable);
	}

}
