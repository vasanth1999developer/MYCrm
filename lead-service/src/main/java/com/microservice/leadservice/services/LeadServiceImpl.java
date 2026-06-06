package com.microservice.leadservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microservice.leadservice.bean.LeadBO;
import com.microservice.leadservice.models.entity.LeadVO;
import com.microservice.leadservice.repository.LeadRepository;

import lombok.extern.log4j.Log4j2;
@Service
@Transactional
@Log4j2
public class LeadServiceImpl implements LeadService {


	@Autowired
	private LeadRepository leadRepository;


	@Override
	public LeadBO createLead(LeadBO leadBo)throws Exception{

		LeadVO leadVo = new LeadVO();

		try {

			BeanUtils.copyProperties(leadBo, leadVo);

			if(leadBo.getStatus().equalsIgnoreCase("progress")) {
				leadVo.setStatus(false);
			}else {
				leadVo.setStatus(true);
			}

			leadVo.setDelete(leadBo.isDelete());
			leadVo = leadRepository.save(leadVo);
			BeanUtils.copyProperties(leadVo, leadBo);

		} catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return leadBo;
	}

	@Override
	public List<LeadVO> getAllLeadsForImport()throws Exception {
		List<LeadBO> leadBO = new ArrayList<>();
		List<LeadVO> leadVO = new ArrayList<>();

		try {

			leadVO = leadRepository.leadFindAll();
	return leadVO;
		//}
		}
catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return leadVO;
	}
	@Override
	public List<LeadVO> getAllLeads()throws Exception {
		List<LeadBO> leadBO = new ArrayList<>();
		List<LeadVO> leadVO = new ArrayList<>();

		try {

			leadVO = leadRepository.leadFindAll();

	return leadVO;
		//}
		}
catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return leadVO;
	}

	@Override
	public LeadBO updateLead(LeadBO leadBo)throws Exception {

		LeadVO leadVo = new LeadVO();

		try {

			BeanUtils.copyProperties(leadBo, leadVo);

			if(leadBo.getStatus().equalsIgnoreCase("progress")) {
				leadVo.setStatus(false);
			}else {
				leadVo.setStatus(true);
			}

			leadVo.setDelete(leadBo.isDelete());
			leadVo = leadRepository.save(leadVo);
			leadBo.setLeadId(leadVo.getLeadId());
			BeanUtils.copyProperties(leadVo, leadBo);

		}catch (Exception e) {
			e.printStackTrace();
			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return leadBo;
	}


	@Override
	public LeadBO getLeadById(long leadId) throws Exception{

		LeadBO leadBo=new LeadBO();
		LeadVO leadVO=new LeadVO();

		try {

			leadVO=leadRepository.getById(leadId);
			BeanUtils.copyProperties(leadVO, leadBo);

			if(leadVO.isStatus()==false) {
				leadBo.setStatus("progress");
			}else {
				leadBo.setStatus("finished");
			}

		}catch (Exception e) {
			e.printStackTrace();

			if(log.isInfoEnabled()) {
				log.info(e.getMessage(),e);

			}
		}

		return leadBo;
	}

	@Override
	public boolean deleteLead(long leadId)throws Exception {

		int number;

		try {

			number	= leadRepository.leadDeleteById(leadId);
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

	@Override
    public     Page<LeadVO> listLeads(int pageIndex, int pageSize, String column, String order, String text)
 {
        Page<LeadBO> pageBo = null;
        try {
            Sort sort;
            if(order!=null && !order.isEmpty())
            {
                if(order.equals("asc"))
                {
                    sort=Sort.by(column).ascending();
                }
                else
                {
                    sort=Sort.by(column).descending();
                }
            }
            else
            {
                sort=Sort.unsorted();
            }
            PageRequest pageable=PageRequest.of(pageIndex, pageSize, sort);
            if(text!=null && !text.isEmpty())
            {
                return leadRepository.findAllByIsDeleteAndFirstNameContainingIgnoreCaseOrProductNameContainingIgnoreCaseOrEmployeeNameContainingIgnoreCase(false,text,text,text,pageable);
            }
            else
            {
                return leadRepository.findAllByIsDelete(false,pageable);
            }
            
            
        } catch (Exception e) {
            if(log.isDebugEnabled()) {
                log.info(e.getMessage(),e);
            }
        }
        return null;

    }

}
