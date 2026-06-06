package com.microservice.inventory.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.microservice.inventory.entity.PriceBookVO;
import com.microservice.inventory.entity.ProductTypeVo;
import com.microservice.inventory.entity.ProductVo;
import com.microservice.inventory.entity.PurchaseOrderVO;
import com.microservice.inventory.entity.SupplierVO;
import com.microservice.inventory.model.PriceBookBO;
import com.microservice.inventory.model.ProductBo;
import com.microservice.inventory.model.ProductTypeBo;
import com.microservice.inventory.model.PurchaseOrderBO;
import com.microservice.inventory.model.SupplierBO;

public interface InventoryService {
	
	//***************************************** PRODUCT TYPE *********************************************
	
	Boolean isDuplicateProductType(String productType);
	
	ProductTypeBo createProductType(ProductTypeBo productTypeBo);

	List<ProductTypeVo> getAllProductType();

	ProductTypeBo updateProductType(ProductTypeBo productTypeBo);

	boolean deleteProductType(long productTypeId);

	ProductTypeVo getSingleProductType(long productTypeId);
	
	Page<ProductTypeBo> listProductType(int pageIndex, int pageSize,String sortOrder,String searchText);
	
	//***************************************** PRODUCT **********************************************
	
	Boolean isDuplicateProduct(String product);
	
	ProductBo createProduct(ProductBo productBo);

	List<ProductVo> getAllProduct();

	ProductBo updateProduct(ProductBo productBo);

	boolean deleteProduct(long productId);

	ProductVo getSingleProduct(long productId);
	
	Page<ProductBo> listProduct(int pageIndex, int pageSize,String columnName,String sortOrder,String searchText);
	
	ProductBo changeProductOwner(ProductBo productBo);
	
	//***************************************** PRICE BOOK **********************************************
	
	Boolean isDuplicatePriceBook(String priceBookName);
	
	boolean deletePriceBook(int priceBookId);

	PriceBookBO updatePriceBook(PriceBookBO priceBookBO);

	List<PriceBookVO> getAllPriceBook();

	PriceBookBO createPriceBook(PriceBookBO priceBookBO);

	PriceBookBO getPriceBookById(int priceBookId);
	
	Page<PriceBookBO> listPriceBook(int pageIndex, int pageSize,String columnName,String sortOrder,String searchText);
	
	PriceBookBO changePriceBookOwner(PriceBookBO priceBookBO);

	//***************************************** SUPPLIER ************************************************
	
	Boolean isDuplicateSupplier(String supplier);
	
	SupplierBO createSupplier(SupplierBO supplierBO);

	List<SupplierVO> getAllSupplier();

	SupplierBO updateSupplier(SupplierBO supplierBO);

	boolean deleteSupplier(long supplierId);

	SupplierVO getSingleSupplier(long supplierId);
	
	Page<SupplierBO> listSupplier(int pageIndex, int pageSize,String columnName,String sortOrder,String searchText);
	
	SupplierBO changeSupplierOwner(SupplierBO supplierBO);
	
	//***************************************** PURCHASE ORDER **********************************************

	Boolean isDuplicateSubject(String subject);
	
	PurchaseOrderBO createPurchaseOrder(PurchaseOrderBO purchaseOrderBO);

	List<PurchaseOrderVO> getAllPurchaseOrder();

	PurchaseOrderBO updatePurchaseOrder(PurchaseOrderBO purchaseOrderBO);

	boolean deletePurchaseOrder(long purchaseOrderId);

	PurchaseOrderVO getSinglePurchaseOrder(long purchaseOrderId);
	
	Page<PurchaseOrderBO> listPurchaseOrder(int pageIndex, int pageSize,String columnName,String sortOrder,String searchText);


	

	
	




}
