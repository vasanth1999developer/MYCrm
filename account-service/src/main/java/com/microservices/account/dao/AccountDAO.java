package com.microservices.account.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.microservices.account.entity.AccountVO;

public interface AccountDAO {

	AccountVO createAccount(AccountVO vo);

	List<AccountVO> getAllAccount();

	AccountVO updateAccount(AccountVO vo);

	AccountVO getAccountById(int id);

	AccountVO deleteaccount(AccountVO vo);

	Page<AccountVO> searchNameList(String firstName, Pageable pageable);

	Page<AccountVO> searchEmailIdList(String accountName, Pageable pageable);

	Page<AccountVO> findAllList(Pageable pageable);

}
