package com.clinch.crm.account.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.clinch.crm.account.entity.AccountVO;
import com.clinch.crm.account.model.AccountBO;

public interface AccountService {

	AccountBO createAccount(AccountBO bo);

	List<AccountBO> getAllAccount();

	AccountBO updateAccount(AccountBO bo);

	AccountBO getAccountId(int id);

	AccountBO deletAccount(AccountBO bo);

	List<AccountVO> searchAccountsByOwner(String owner);

	List<AccountVO> searchAccountName(String accountName);

	Page<AccountBO> listAccount(int pageIndex, int pageSize, String sorter, String firstName, String accountName,
			String columnName);

}
