package com.microservice.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservice.inventory.entity.ProductVo;



@Repository
public interface ProductRepository extends JpaRepository<ProductVo, Long>{

	@Modifying
    @Query("update ProductVo set isDelete=true,isActive=false where productId =?1")
	void deleteProduct(long productId);

	@Modifying
    @Query("from ProductVo where isDelete =false")
	List<ProductVo> getAllProduct();

	Page<ProductVo> findAllByIsDelete(boolean isDelete,Pageable pageable);
	
	Page<ProductVo> findAllByProductNameContainingIgnoreCaseAndIsDelete(String productName,boolean isDelete,Pageable pageable);
	
	List<ProductVo> findAllByProductNameIgnoreCaseAndIsDelete(String productName,boolean isDelete);
}
