package com.microservice.leadservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservice.leadservice.models.entity.LeadVO;
@Repository
public interface LeadRepository extends JpaRepository<LeadVO,Long> {
	@Modifying
    @Query("update LeadVO set isDelete=true where leadId =?1")
	int leadDeleteById(long leadId);
	
	@Modifying
    @Query("from LeadVO where isDelete =false") 
	List<LeadVO>leadFindAll();


	long countByEmployeeName(String text);

	Page<LeadVO> findAllByIsDeleteAndFirstNameContainingIgnoreCaseOrProductNameContainingIgnoreCaseOrEmployeeNameContainingIgnoreCase(
			boolean b, String text, String text2, String text3, PageRequest pageable);

	Page<LeadVO> findAllByIsDelete(boolean b, PageRequest pageable);
	
    @Query(value="SELECT *FROM lead_table WHERE email =:email and is_delete=false",nativeQuery=true)
	LeadVO findByEmail(@Param("email")String email);

	
}
