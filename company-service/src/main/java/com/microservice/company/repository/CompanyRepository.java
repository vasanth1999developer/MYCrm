package com.microservice.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservice.company.entity.CompanyVO;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyVO,Long>  {
	
	@Modifying
    @Query("update CompanyVO set isDelete=true where companyId =?1")
	int companyDeleteById(Long companyId);
	
	@Modifying
    @Query("from CompanyVO where isDelete =false") 
	List<CompanyVO>companyFindAll();

}