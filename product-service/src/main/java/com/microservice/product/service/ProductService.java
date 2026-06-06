package com.microservice.product.service;

import java.util.List;

import com.microservice.product.bo.ProductBo;
import com.microservice.product.vo.ProductVo;

public interface ProductService {

	ProductBo createProduct(ProductBo productBo);

	List<ProductVo> getAllProduct();

	ProductBo updateProduct(ProductBo productBo);

	boolean deleteProduct(long productId);

	ProductVo getSingleProduct(long productId);

	boolean isStockAvailable(Long productId, int requiredUnits);

}
