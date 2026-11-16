package com.clinch.crm.salesorder.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.clinch.crm.salesorder.been.QuotesBO;

public interface QuotesService {

	QuotesBO createQuotes(QuotesBO quotesBo);

	Page<QuotesBO> getAllQuotes(int page, int size);

	QuotesBO getQuotes(long quotesId);

	int deleteQuotes(long quotesId);

	QuotesBO updateQuotes(QuotesBO quotesBo);

}
