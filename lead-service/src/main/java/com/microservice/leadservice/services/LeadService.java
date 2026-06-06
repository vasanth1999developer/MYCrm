package com.microservice.leadservice.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.microservice.leadservice.bean.LeadBO;
import com.microservice.leadservice.models.entity.LeadVO;

public interface LeadService {

	LeadBO createLead(LeadBO leadBo) throws Exception;
	LeadBO updateLead(LeadBO leadBo)throws Exception;

	boolean deleteLead(long leadId)throws Exception;

	LeadBO getLeadById(long leadId)throws Exception;

	

	Page<LeadVO> listLeads(int pageIndex, int pageSize, String column, String order, String text);
	

	List<LeadVO> getAllLeadsForImport()throws Exception;
	List<LeadVO> getAllLeads()throws Exception;


	
	
	

}
