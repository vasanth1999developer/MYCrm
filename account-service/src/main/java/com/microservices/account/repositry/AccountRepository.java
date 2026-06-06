package com.microservices.account.repositry;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.microservices.account.entity.AccountVO;
@Component
public interface AccountRepository extends JpaRepository<AccountVO,Integer>{

	List<AccountVO> findByAccountOwnerContainingIgnoreCase(String owner);

	List<AccountVO> findByAccountName(String accountName);

	Page<AccountVO> findAllByAccountNameContainingAndIsdeleteFalse(String firstName, Pageable pageable);

	//Page<AccountVO> findAllByAccountOwnerContainingAndIsdeleteFalse(String accountOwner, Pageable pageable);

	Page<AccountVO> findAllByIsdeleteFalse(Pageable pageable);

}
