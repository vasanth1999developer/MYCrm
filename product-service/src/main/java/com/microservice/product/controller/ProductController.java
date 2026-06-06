package com.microservice.product.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.product.bo.ProductBo;
import com.microservice.product.service.ProductService;
import com.microservice.product.vo.ProductVo;

@RestController
@RequestMapping("/inventory/product")
//@CrossOrigin(origins = "*")
@Endpoint(id = "product-endpoint")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping("/create-product")
	public ResponseEntity<?> createProduct(@RequestBody ProductBo productBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				productBo.setCreatedBy(number);
			}
			productBo = productService.createProduct(productBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(productBo);

	}

	@ReadOperation
	@GetMapping("/get-all-product")
	public ResponseEntity<?> getAllProduct() {
		List<ProductVo> productVo = new ArrayList<>();
		try {
			productVo = productService.getAllProduct();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok(productVo);

	}

	@PutMapping("/update-product")
	public ResponseEntity<?> updateProduct(@RequestBody ProductBo productBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				productBo.setModifiedBy(number);
			}
			productBo = productService.updateProduct(productBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(productBo);

	}

	@DeleteMapping("/delete-product/{id}")
	public ResponseEntity<Boolean> deleteProduct(@PathVariable("id") long productId) {
		boolean status = false;
		try {
			status = productService.deleteProduct(productId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("/get-single-id/{productId}")
	public ResponseEntity<?> getSingleProduct(@PathVariable("productId") long productId) {
		ProductVo productVo = new ProductVo();
		try {

			productVo = productService.getSingleProduct(productId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(productVo);

	}

	@GetMapping("/check-stock")
	public ResponseEntity<Boolean> checkProductStock(@RequestParam Long productId, @RequestParam int requiredUnits) {

//	    try {
//	        
//	        Thread.sleep(10000);
//	    } catch (InterruptedException e) {
//	        Thread.currentThread().interrupt(); 
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//	    }

		boolean isAvailable = productService.isStockAvailable(productId, requiredUnits);
		return ResponseEntity.ok(isAvailable);
	}
}
