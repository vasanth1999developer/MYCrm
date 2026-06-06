package com.microservice.opportunity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.opportunity.bo.OpportunityBo;
import com.microservice.opportunity.models.entity.OpportunityVo;

import feign.Param;
@Transactional
public interface OpportunityRepository extends JpaRepository<OpportunityVo, Long> {		
	

	@Modifying
	@Query("update OpportunityVo set isDelete=true where opportunityId =?1")
	 int DeleteById(long id);
	
	
	
	@Modifying
	@Query("from OpportunityVo where isDelete =false ")
	 List<OpportunityVo> findAll();
	
	
	
//	@Query( value = "SELECT * FROM employee_vo e where e.email_id like %:emailId%",  nativeQuery = true)
//    List<EmployeeVo> searchByEmail(@Param("emailId")String emailId);
	
	
	
	@Query( value ="SELECT * FROM opportunity o WHERE o.email_address=:emailAddress and o.is_delete = false",nativeQuery = true)
	OpportunityVo findByEmail(@Param("emailAddress")String emailAddress);
	
	
	
	 List<OpportunityVo> findByFirstNameContainingIgnoreCaseAndIsDeleteFalse(String firstName);

	 
	List<OpportunityVo> findByEmailAddressContainingIgnoreCaseAndIsDeleteFalse(String emailId);

	
	boolean save(OpportunityBo opportunityVo);


	@Modifying
    @Query("from OpportunityVo where isDelete =false")
	List<OpportunityVo> opportunityFindAll();

	
	Page<OpportunityVo> findAllByIsDeleteFalse(Pageable pageable);

	
	Page<OpportunityVo> findAllByFirstNameContainingAndIsDeleteFalse(String searchName, Pageable pageable);



	Page<OpportunityVo> findAllByEmailAddressContainingAndIsDeleteFalse(String searchEmailId, Pageable pageable);

	
}
