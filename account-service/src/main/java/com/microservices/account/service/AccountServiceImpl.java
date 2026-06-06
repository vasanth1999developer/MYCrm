package com.microservices.account.service;


import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microservices.account.dao.AccountDAO;
import com.microservices.account.entity.AccountVO;
import com.microservices.account.model.AccountBO;
import com.microservices.account.repositry.AccountRepository;

import lombok.extern.log4j.Log4j2;




@Service
@Transactional
@Log4j2
public class AccountServiceImpl implements AccountService{
	@Autowired
	private AccountDAO dao;
	

	 @Autowired
	 AccountRepository accountRepository;


	@Override
	public AccountBO createAccount(AccountBO bo) {
	AccountVO vo=new AccountVO();
	try {
		BeanUtils.copyProperties(bo, vo);
		vo.setIsdelete(false);
		vo=dao.createAccount(vo);
		if(null!=vo) {
			bo.setAccountId(vo.getAccountId());
			return bo;
		}
	}catch(Exception e) {
		e.printStackTrace();
	}
	
		return null;
	}

	@Override
	public List<AccountBO> getAllAccount() {
		List<AccountVO> volist=new ArrayList<>();
		List<AccountBO> bolist=new ArrayList<>();
		try {
			volist=dao.getAllAccount();
			if(null!=volist)
			{
				for(AccountVO vo:volist) {
					AccountBO bo=new AccountBO();

					bo.setAccountId(vo.getAccountId());
					bo.setAccountName(vo.getAccountName());
					bo.setAccountOwner(vo.getAccountOwner());
					bo.setAnnualRevenue(vo.getAnnualRevenue());
					bo.setCity(vo.getCity());
					bo.setContactNumber(vo.getContactNumber());
					bo.setCountry(vo.getCountry());
					bo.setDescription(vo.getDescription());
					bo.setEmail(vo.getEmail());
					bo.setIndustry(vo.getIndustry());
					bo.setNoOfEmploye(vo.getNoOfEmploye());
					bo.setParentAccount(vo.getParentAccount());
					bo.setPincode(vo.getPincode());
					bo.setSalutation(vo.getSalutation());
					bo.setState(vo.getState());
					bo.setStreet(vo.getStreet());

					bolist.add(bo);
					
				
				}
				return bolist;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public AccountBO updateAccount(AccountBO bo) {
		AccountVO vo=new AccountVO();
		try {
			vo.setAccountId(bo.getAccountId());
			BeanUtils.copyProperties(bo, vo);
			vo=dao.updateAccount(vo);
			if(null!=vo) {
				bo.setAccountId(vo.getAccountId());
				return bo;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountBO getAccountId(int id) {
		AccountVO vo=new AccountVO();
		try {
			vo=dao.getAccountById(id);
			if(null!=vo) {
				AccountBO bo=new AccountBO();
				BeanUtils.copyProperties(vo, bo);
				return bo;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	@Override
	public AccountBO deletAccount(AccountBO bo) {
	try {
		AccountVO vo=new AccountVO();
		vo.setAccountId(bo.getAccountId());
		vo.setIsdelete(true);
		vo=dao.deleteaccount(vo);
	}catch(Exception e) {
		e.printStackTrace();
	}
		return null;
	}
	
	@Override
	public List<AccountVO> searchAccountsByOwner(String owner) {
		// TODO Auto-generated method stub
		 return accountRepository.findByAccountOwnerContainingIgnoreCase(owner);
			}

	@Override
	public List<AccountVO> searchAccountName(String accountName) {
		// TODO Auto-generated method stub
		 return accountRepository.findByAccountName(accountName);
	}

	@Override
	public Page<AccountBO> listAccount(int pageIndex, int pageSize, String sorterOrder, String firstName, String accountName,
			String columnName) {
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
            pageable = (Pageable) PageRequest.of(pageIndex, pageSize, sort);
            
            // Fetch data based on search criteria
            Page<AccountVO> pageVo;
            if (firstName != null && !firstName.isEmpty() && (accountName == null || accountName.isEmpty())) {
                pageVo = dao.searchNameList(firstName, pageable);
            } else if (accountName != null && !accountName.isEmpty() && (firstName == null || firstName.isEmpty())) {
                pageVo = dao.searchEmailIdList(accountName, pageable);
            } else {
                pageVo = dao.findAllList(pageable);
            }

            // Convert Page<OpportunityVo> to Page<OpportunityBo>
            return pageVo.map(vo -> {
            	AccountBO bo = new AccountBO();
                BeanUtils.copyProperties(vo, bo);
                return bo;
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (log.isDebugEnabled()) {
                log.info(e.getMessage(), e);
            }
            // Return an empty page to ensure that the method returns a consistent type
        
        }
		return null;
	}

}
