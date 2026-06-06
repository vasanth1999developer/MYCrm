package com.microservice.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservice.inventory.entity.ProductTypeVo;



@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeVo, Long>{

	@Modifying
    @Query("from ProductTypeVo where isDelete =false")
	List<ProductTypeVo> getAllProductType();

	@Modifying
    @Query("update ProductTypeVo set isDelete=true,isActive=false where productTypeId =?1")
	void deleteProductType(long productTypeId);

    Page<ProductTypeVo> findAllByIsDelete(boolean isDelete,Pageable pageable);
	
	Page<ProductTypeVo> findAllByProductTypeContainingIgnoreCaseAndIsDelete(String productType,boolean isDelete,Pageable pageable);
	
	List<ProductTypeVo> findAllByProductTypeIgnoreCaseAndIsDelete(String productType,boolean isDelete);
	

}
