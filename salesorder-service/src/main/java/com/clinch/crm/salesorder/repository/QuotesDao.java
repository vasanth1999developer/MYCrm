package com.clinch.crm.salesorder.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.clinch.crm.salesorder.entity.QuotesEntity;

public interface QuotesDao {

	QuotesEntity createQuotes(QuotesEntity quotesVo);
	String generateUnique5DigitNumber();
	boolean isUniqueInvoiceNumber(String invoiceNumber);
	Page<QuotesEntity> getAllAccounts(int page, int size);
	QuotesEntity getQuotes(long quotesId);
	int deleteQuotes(long quotesId);
	QuotesEntity updateQuotes(QuotesEntity quotesVo);

}
