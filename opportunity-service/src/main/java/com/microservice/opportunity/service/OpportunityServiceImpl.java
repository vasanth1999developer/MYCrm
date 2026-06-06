package com.microservice.opportunity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.opportunity.bo.OpportunityBo;
import com.microservice.opportunity.dao.OpportunityDao;
import com.microservice.opportunity.models.entity.OpportunityVo;
import com.microservice.opportunity.repository.OpportunityRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class OpportunityServiceImpl implements OpportunityService {

	@Autowired
	OpportunityDao opportunityDao;
	
	@Autowired
	OpportunityRepository opportunityRepository;


	@Override
	public List<OpportunityBo> findAll() {

		try {
			List<OpportunityVo> opportunityVoList = opportunityDao.findAll();
			if (opportunityVoList != null && !opportunityVoList.isEmpty()) {
				return opportunityVoList.stream().map(vo -> {
					OpportunityBo bo = new OpportunityBo();
					BeanUtils.copyProperties(vo, bo);
					return bo;
				}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			// e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
			log.error("Error retrieving data from DAO", e); // Use error level logging for exceptions
			return null; // Return an empty list on error
		}
		return null;
	}

	@Override
	public boolean save(OpportunityBo opportunityBo) {

		boolean status = false;
		OpportunityVo opportunityVo = new OpportunityVo();
		try {
			BeanUtils.copyProperties(opportunityBo, opportunityVo);
			status = opportunityDao.save(opportunityVo);

		} catch (BeansException e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public OpportunityBo findById(Long id) {

		OpportunityVo opportunityVo = null;
		OpportunityBo opportunityBo = new OpportunityBo();
		try {
			opportunityVo = opportunityDao.findById(id);
			if (null != opportunityVo) {
				BeanUtils.copyProperties(opportunityVo, opportunityBo);
				if (null != opportunityBo) {
					return opportunityBo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	public boolean deleteById(Long id) {
		boolean status = false;
		try {
			status = opportunityDao.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return status;
	}

	@Override

	public List<OpportunityVo> getAllOpportunity() throws Exception {
		
		List<OpportunityVo> opportunityVo = new ArrayList<>();

		try {

			opportunityVo = opportunityRepository.opportunityFindAll();
			return opportunityVo;

		} catch (Exception e) {
			e.printStackTrace();
			if (log.isInfoEnabled()) {
				log.info(e.getMessage(), e);

			}
		}

		return opportunityVo;
	}


	public Page<OpportunityBo> listOpportunity(int pageIndex, int pageSize, String sorterOrder, String searchName,
	        String searchEmailId, String columnName) {

	    Pageable pageable = null;
	    Sort sort = null;
	    try {
	        if (columnName == null || columnName.isEmpty()) {
	            // If no valid column name is provided, fallback to a default or skip sorting
	            sort = Sort.unsorted(); // Or use Sort.by("defaultColumnName").ascending() if there is a sensible default
	        } else {
	            // Determine sort direction
	            if ("asc".equalsIgnoreCase(sorterOrder)) {
	                sort = Sort.by(columnName).ascending();
	            } else if ("desc".equalsIgnoreCase(sorterOrder)) {
	                sort = Sort.by(columnName).descending();
	            } else {
	                // If no valid sorter order is specified, default to ascending or unsorted
	                sort = Sort.by(columnName).ascending();
	            }
	        }

	        // Apply the sorting to pageable
	        pageable = PageRequest.of(pageIndex, pageSize, sort);
	        
	        // Fetch data based on search criteria
	        Page<OpportunityVo> pageVo;
	        if (searchName != null && !searchName.isEmpty() && (searchEmailId == null || searchEmailId.isEmpty())) {
	            pageVo = opportunityDao.searchNameList(searchName, pageable);
	        } else if (searchEmailId != null && !searchEmailId.isEmpty() && (searchName == null || searchName.isEmpty())) {
	            pageVo = opportunityDao.searchEmailIdList(searchEmailId, pageable);
	        } else {
	            pageVo = opportunityDao.findAllList(pageable);
	        }

	        // Convert Page<OpportunityVo> to Page<OpportunityBo>
	        return pageVo.map(vo -> {
	            OpportunityBo bo = new OpportunityBo();
	            BeanUtils.copyProperties(vo, bo);
	            return bo;
	        });

	    } catch (Exception e) {
	        e.printStackTrace();
	        if (log.isDebugEnabled()) {
	            log.info(e.getMessage(), e);
	        }
	        // Return an empty page to ensure that the method returns a consistent type
	        return new PageImpl<>(new ArrayList<>(), pageable, 0);
	    }
	}

}
