package com.microservice.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.microservice.inventory.entity.PriceBookVO;


public interface PriceBookRepository extends JpaRepository<PriceBookVO, Integer>{
	@Modifying
	@Query("update PriceBookVO set isDelete =true where id =?1")
	 void priceBookDeleteById(int priceBookId);
	
	@Modifying
	@Query("from PriceBookVO where isDelete =false ")
	 List<PriceBookVO> priceBookfindAll();

	Page<PriceBookVO> findAllByIsDelete(boolean isDelete,Pageable pageable);
	
	Page<PriceBookVO> findAllByPriceBookNameContainingIgnoreCaseAndIsDelete(String priceBookName,boolean isDelete,Pageable pageable);
	
	List<PriceBookVO> findAllByPriceBookNameIgnoreCaseAndIsDelete(String priceBookName,boolean isDelete);
}
