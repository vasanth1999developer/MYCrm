package com.microservice.salesorder.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.salesorder.been.QuotesBO;
import com.microservice.salesorder.entity.QuotesEntity;
import com.microservice.salesorder.repository.QuotesDao;

@Service
@Transactional
public class QuotesServiceImpl implements QuotesService{
@Autowired
	private QuotesDao quotesDao;
@Override
	public QuotesBO createQuotes(QuotesBO quotesBo) {
        QuotesEntity quotesVo = new QuotesEntity();
        LocalDateTime datetime1 = LocalDateTime.now();  
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
		String formatDateTime = datetime1.format(format); 
		try {
//			quotesVo.setIsDelete(false);
//			quotesVo.setCreatedBy(quotesBo.getAssigntedTo());
//			quotesVo.setCreatedOn(formatDateTime);
//			quotesVo.setQuotesUniNumber(Long.parseLong(quotesDao.generateUnique5DigitNumber()));
			BeanUtils.copyProperties(quotesBo, quotesVo);
			
			quotesVo.setIsDelete(false);
			quotesVo.setCreatedBy(quotesBo.getAssigntedTo());
			quotesVo.setCreatedOn(formatDateTime);
			quotesVo.setQuotesUniNumber(Long.parseLong(quotesDao.generateUnique5DigitNumber()));
			quotesVo=quotesDao.createQuotes(quotesVo);
            if(null!=quotesVo) {
				BeanUtils.copyProperties(quotesVo, quotesBo);
                return quotesBo;
			}
}
		catch(Exception e) {
}
return null;
	}
	
	@Override
	public QuotesBO updateQuotes(QuotesBO quotesBo) {
		QuotesEntity quotesVo = new QuotesEntity();
        LocalDateTime datetime1 = LocalDateTime.now();  
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
		String formatDateTime = datetime1.format(format); 
		try {
			
			
		
			BeanUtils.copyProperties(quotesBo, quotesVo);
			quotesVo.setIsDelete(false);
			quotesVo.setModifiedBy(quotesBo.getAssigntedTo());
			quotesVo.setModifiedOn(formatDateTime);
			quotesVo.setQuotesUniNumber(quotesBo.getQuotesUni());
			quotesVo=quotesDao.updateQuotes(quotesVo);
			if(null!=quotesVo) {
				BeanUtils.copyProperties(quotesVo, quotesBo);
                return quotesBo;
			}
			
		}
		catch(Exception e) {
			
		}
		
		
		
		
		
		
		
		return null;
	}

	@Override
	public Page<QuotesBO> getAllQuotes(int page, int size) {

		List<QuotesBO>quotesBoList = new ArrayList<>();
		Page<QuotesEntity>quotesVoList = null;

		try {
			quotesVoList= quotesDao.getAllAccounts(page,size);
			if(null!=quotesVoList) {

				for(QuotesEntity vo:quotesVoList) {
                       QuotesBO bo = new QuotesBO();
                       
                       bo.setAssigntedTo(vo.getAssigntedTo());
                      bo.setQuotesUni(vo.getQuotesUniNumber());
                       BeanUtils.copyProperties(vo, bo);
                       quotesBoList.add(bo);
                       }

			}
               return (new PageImpl<>(quotesBoList, quotesVoList.getPageable(), quotesVoList.getTotalElements()));
		}
		    catch(Exception e) {

		}

		return null;
	}

	@Override
	public QuotesBO getQuotes(long quotesId) {
		
		QuotesEntity quotesEntity = new QuotesEntity();
		
		try {
			QuotesBO quotesBo = new QuotesBO();
			quotesEntity=quotesDao.getQuotes(quotesId);
			
			BeanUtils.copyProperties(quotesEntity, quotesBo);
			quotesBo.setQuotesUni(quotesEntity.getQuotesUniNumber());
			
			return quotesBo;
			
		}
		catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return null;
	}

	@Override
	public int deleteQuotes(long quotesId) {
		QuotesEntity quotesEntity = new QuotesEntity();
		
		try {
			QuotesBO quotesBo = new QuotesBO();
			int updatedCount=quotesDao.deleteQuotes(quotesId);
			
			return updatedCount;
			
		}
		catch(Exception e) {
			
			
		}
		
		return 0;
	}

	

}
