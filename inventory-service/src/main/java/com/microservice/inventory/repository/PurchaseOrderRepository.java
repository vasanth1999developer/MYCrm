package com.microservice.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.microservice.inventory.entity.PurchaseOrderVO;


public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderVO, Long>{

	@Modifying
    @Query("from PurchaseOrderVO where isDelete =false")
	List<PurchaseOrderVO> getAllPurchaseOrder();

	@Modifying
    @Query("update PurchaseOrderVO set isDelete=true,isActive=false where purchaseOrderId =?1")
	void deletePurchaseOrder(long purchaseOrderId);

	Page<PurchaseOrderVO> findAllByIsDelete(boolean isDelete,Pageable pageable);
	
	Page<PurchaseOrderVO> findAllBySubjectContainingIgnoreCaseAndIsDelete(String salesOrderName,boolean isDelete,Pageable pageable);
	
	PurchaseOrderVO findBySubjectIgnoreCaseAndIsDelete(String subject,boolean isDelete);

}
