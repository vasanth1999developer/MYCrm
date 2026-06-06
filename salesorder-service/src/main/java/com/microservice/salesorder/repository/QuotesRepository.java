package com.microservice.salesorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.microservice.salesorder.entity.QuotesEntity;


@Repository
public interface QuotesRepository extends JpaRepository<QuotesEntity,Long>{

	


	QuotesEntity quotesEntity = null;


	@Query("SELECT e FROM QuotesEntity e WHERE isDelete = false")
    Page<QuotesEntity> findBySomeFieldWithPagination(Pageable pageable);

	
	 @Query("SELECT c FROM QuotesEntity c WHERE quotesUniNumber = :quotesUni")
	 QuotesEntity findByquotesUniNumber(long quotesUni);

	

	
	
	
}
