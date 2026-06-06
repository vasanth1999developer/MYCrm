package com.microservice.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservice.inventory.entity.SupplierVO;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierVO, Long> {

	@Modifying
	@Query("from SupplierVO where isDelete =false")
	List<SupplierVO> getAllSupplier();

	@Modifying
	@Query("update SupplierVO set isDelete=true,isActive=false where supplierId =?1")
	void deleteSupplier(long supplierId);

	Page<SupplierVO> findAllByIsDelete(boolean isDelete, Pageable pageable);

	Page<SupplierVO> findAllBySupplierNameContainingIgnoreCaseAndIsDelete(String supplierName, boolean isDelete,
			Pageable pageable);

	List<SupplierVO> findAllBySupplierNameIgnoreCaseAndIsDelete(String supplierName,boolean isDelete);
}
