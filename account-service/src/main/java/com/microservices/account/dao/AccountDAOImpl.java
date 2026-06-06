package com.microservices.account.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.microservices.account.entity.AccountVO;
import com.microservices.account.repositry.AccountRepository;



@Repository
public class AccountDAOImpl implements AccountDAO {
	
	@Autowired
	private EntityManager entitymanager;
	
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public AccountVO createAccount(AccountVO vo) {
		try {
			entitymanager.persist(vo);
			if(0!=vo.getAccountId()) {
				return vo;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<AccountVO> getAllAccount() {
		List<AccountVO> volist=new ArrayList<>();
		try {
			CriteriaBuilder criteriaBuilder=entitymanager.getCriteriaBuilder();
			CriteriaQuery<AccountVO> cq=criteriaBuilder.createQuery(AccountVO.class);
			Root<AccountVO> root=cq.from(AccountVO.class);
			cq.select(root)
			.where(criteriaBuilder.equal(root.get("isdelete"), false));
			Query<AccountVO> query=(Query<AccountVO>) entitymanager.createQuery(cq);
			volist=query.getResultList();
			if(null!=volist&& volist.size()>0&&!volist.isEmpty()) {
				return volist;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountVO updateAccount(AccountVO vo) {
		try {
			entitymanager.merge(vo);
			if(null!=vo) {
				return vo;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountVO getAccountById(int id) {
		AccountVO vo=new AccountVO();
		try {
			CriteriaBuilder criteriaBuilder=entitymanager.getCriteriaBuilder();
			CriteriaQuery<AccountVO> cq=criteriaBuilder.createQuery(AccountVO.class);
			Root<AccountVO> root=cq.from(AccountVO.class);
			
			cq.select(root).where(criteriaBuilder.equal(root.get("accountId"), id));
			Query<AccountVO> query=(Query<AccountVO>) entitymanager.createQuery(cq);
			vo=query.getSingleResult();
			if(null!=vo) {
				return vo;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountVO deleteaccount(AccountVO vo) {
		int result = 0;
		String deletequery = "UPDATE AccountVO f set f.isdelete=:isdelete where f.accountId=:accountId";
		try {
			Query query = (Query) entitymanager.createQuery(deletequery);
			query.setParameter("isdelete", true);
			query.setParameter("accountId", vo.getAccountId());
			result = query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
    public Page<AccountVO> searchNameList(String firstName, Pageable pageable) {

        return null;
    }

    @Override
    public Page<AccountVO> searchEmailIdList(String accountOwner, Pageable pageable) {

        return accountRepository.findAllByAccountNameContainingAndIsdeleteFalse(accountOwner, pageable);
    }

    @Override
    public Page<AccountVO> findAllList(Pageable pageable) {

        return accountRepository.findAllByIsdeleteFalse(pageable);
    }
}
