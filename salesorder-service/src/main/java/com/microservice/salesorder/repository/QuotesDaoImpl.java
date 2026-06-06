package com.microservice.salesorder.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.microservice.salesorder.entity.QuotesEntity;

@Repository
public class QuotesDaoImpl implements QuotesDao{
	
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private QuotesRepository quotesRepository ;

	@Override
	public QuotesEntity createQuotes(QuotesEntity quotesVo) {
	try {
			entityManager.persist(quotesVo);
			if(0!=quotesVo.getQuotesId()) {
					return quotesVo;
			}
			}
		catch(Exception e) {
			e.printStackTrace();
			}
		return null;
	}
	
	@Override
	public QuotesEntity updateQuotes(QuotesEntity quotesVo) {
		try {
			quotesRepository.save(quotesVo);
			if(0!=quotesVo.getQuotesId()) {
					return quotesVo;
			}
			}
		catch(Exception e) {
			e.printStackTrace();
			}
		return null;
		
		
		
		
	}
		
	
public String generateUnique5DigitNumber() {
		
		String generatedNumber;
        do {
            generatedNumber = String.format("%05d", new Random().nextInt(90000) + 10000);
        } while (!isUniqueInvoiceNumber(generatedNumber) && generatedNumber.length()==5 && !generatedNumber.isEmpty());

        return generatedNumber;
           
    }

    public boolean isUniqueInvoiceNumber(String invoiceNumber) {
    	
        Query query = entityManager.createQuery("SELECT COUNT(q) FROM QuotesEntity q WHERE quotesUniNumber = :number");
        
     
        query.setParameter("number",Long.parseLong(invoiceNumber));
        Long count =  (Long) query.getSingleResult();
        return count == 0;
    }
@Override
	public Page<QuotesEntity> getAllAccounts(int page, int size) {
		List<QuotesEntity>  quotesVoList= new ArrayList<>();
		try {
            Pageable pageable = PageRequest.of(page, size);
			Page<QuotesEntity> pageBo =null;
			pageBo= quotesRepository.findBySomeFieldWithPagination(pageable);
			return (Page<QuotesEntity>) pageBo;
			}
		catch(Exception e) {
			e.printStackTrace();
			}
		return null;
	}

@Override
	public QuotesEntity getQuotes(long quotesId) {
		QuotesEntity quotesEntity = new QuotesEntity();
		
		try {
			quotesEntity = quotesRepository.findByquotesUniNumber(quotesId);      
			return quotesEntity;
			}
		catch(Exception e) {
			e.printStackTrace();
			}
		return null;
	}

@Override
	public int deleteQuotes(long quotesId) {
		
//	QuotesEntity quotesEntity = new QuotesEntity();
	try {
		
		 Query query = entityManager.createQuery("UPDATE QuotesEntity c SET isDelete = :newValue WHERE quotesUniNumber = :quotesUni");
		
		 query.setParameter("newValue", true);
	        query.setParameter("quotesUni", quotesId);
	        
	        int updatedCount  = query.executeUpdate();
		
		return updatedCount ;
	}
	catch(Exception e) {
		
		e.printStackTrace();
		
	}
	
	
	
	
		return 0;
	}


	
	
	
	

}
