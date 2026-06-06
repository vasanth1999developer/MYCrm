package com.microservice.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.product.bo.ProductBo;
import com.microservice.product.repository.ProductRepository;
import com.microservice.product.vo.ProductVo;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public ProductBo createProduct(ProductBo productBo) {
		ProductVo productVo = new ProductVo();
		try {

			BeanUtils.copyProperties(productBo, productVo);
			productVo.setDelete(false);
			productVo.setActive(true);
			productVo = productRepository.save(productVo);
			BeanUtils.copyProperties(productVo, productBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productBo;
	}

	@Override
	public List<ProductVo> getAllProduct() {
		List<ProductVo> productVo = new ArrayList<>();
		try {
			productVo = productRepository.getAllProduct();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return productVo;
	}

	@Override
	public ProductBo updateProduct(ProductBo productBo) {
		ProductVo productVo = new ProductVo();
		try {
			BeanUtils.copyProperties(productBo, productVo);
			productVo = productRepository.save(productVo);
			productBo.setProductId(productVo.getProductId());
			BeanUtils.copyProperties(productVo, productBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productBo;
	}

	@Override
	public boolean deleteProduct(long productId) {
		ProductVo productVo = new ProductVo();
		boolean status = false;
		try {

			productRepository.deleteProduct(productId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	@Override
	public ProductVo getSingleProduct(long productId) {
		ProductVo productVo = new ProductVo();
		try {
			Optional<ProductVo> product = productRepository.findById(productId);
			productVo = product.get();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return productVo;
	}

	public boolean reduceStock(Long productId, int orderedUnits) {
		ProductVo product = productRepository.findById(productId).orElse(null);

		if (product == null) {

			return false;
		}

		if (product.getAvaliablesStocks() >= orderedUnits) {

			product.setAvaliablesStocks(product.getAvaliablesStocks() - orderedUnits);
			productRepository.save(product);
			return true;
		} else {

			return false;
		}
	}

	@Override
	public boolean isStockAvailable(Long productId, int requiredUnits) {
		Optional<ProductVo> product = productRepository.findById(productId);

		if (product.isPresent()) {
			return product.get().getAvaliablesStocks() >= requiredUnits;
		}
		return false;
	}

}
