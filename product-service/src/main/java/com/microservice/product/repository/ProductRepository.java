package com.microservice.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservice.product.vo.ProductVo;

@Repository
public interface ProductRepository extends JpaRepository<ProductVo, Long> {

	@Modifying
	@Query("update ProductVo set isDelete=true,isActive=false where productId =?1")
	void deleteProduct(long productId);

	@Modifying
	@Query("from ProductVo where isDelete =false")
	List<ProductVo> getAllProduct();

	Optional<ProductVo> findById(Long productId);

}
