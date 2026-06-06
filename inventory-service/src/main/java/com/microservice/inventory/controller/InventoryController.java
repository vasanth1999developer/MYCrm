package com.microservice.inventory.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microservice.inventory.csv.CSVExpoter;
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
import com.microservice.inventory.repository.ProductRepository;
import com.microservice.inventory.repository.SupplierRepository;
import com.microservice.inventory.service.InventoryService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.log4j.Log4j2;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/inventory")
@Log4j2
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	// ***************************************** PRODUCT TYPE
	// **********************************************

	@GetMapping("/check-duplicate-product-type")
	public ResponseEntity<?> isDuplicateProductType(@RequestParam("productType") String productType) {
		try {
			Boolean isDuplicate = inventoryService.isDuplicateProductType(productType);
			if (isDuplicate != null) {
				return ResponseEntity.ok(isDuplicate);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/create-product-type")
	public ResponseEntity<?> createProductType(@RequestBody ProductTypeBo productTypeBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				productTypeBo.setCreatedBy(number);
			}
			productTypeBo = inventoryService.createProductType(productTypeBo);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productTypeBo);

	}

	@ReadOperation
	@GetMapping("/get-all-productType")
	public ResponseEntity<?> getAllProductType() {
		List<ProductTypeVo> productTypeVo = new ArrayList<>();
		try {
			productTypeVo = inventoryService.getAllProductType();
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}

		return ResponseEntity.ok(productTypeVo);

	}

	@PutMapping("/update-productType")
	public ResponseEntity<?> updateProductType(@RequestBody ProductTypeBo productTypeBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				productTypeBo.setModifiedBy(number);
			}
			productTypeBo = inventoryService.updateProductType(productTypeBo);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productTypeBo);

	}

	@DeleteMapping("/delete-productType/{id}")
	public ResponseEntity<Boolean> deleteProductType(@PathVariable("id") long productTypeId) {
		boolean status = false;
		try {
			status = inventoryService.deleteProductType(productTypeId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("/get-productType/{productTypeId}")
	public ResponseEntity<?> getSingleProductType(@PathVariable("productTypeId") long productTypeId) {
		ProductTypeVo productTypeVo = new ProductTypeVo();
		try {

			productTypeVo = inventoryService.getSingleProductType(productTypeId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productTypeVo);

	}

	@GetMapping("list-product-type/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listProductTypes(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			Page<ProductTypeBo> listPage = inventoryService.listProductType(pageIndex, pageSize, sortOrder, searchText);
			if (listPage != null) {
				return ResponseEntity.ok(listPage);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	// ***************************************** PRODUCT
	// **********************************************

	@GetMapping("/check-duplicate-product")
	public ResponseEntity<?> isDuplicateProduct(@RequestParam("productName") String productName) {
		try {
			Boolean isDuplicate = inventoryService.isDuplicateProduct(productName);
			if (isDuplicate != null) {
				return ResponseEntity.ok(isDuplicate);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/create-product")
	public ResponseEntity<?> createProduct(@RequestBody ProductBo productBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				productBo.setCreatedBy(number);
			}
			productBo = inventoryService.createProduct(productBo);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productBo);

	}

	@ReadOperation
	@GetMapping("/get-all-product")
	public ResponseEntity<?> getAllProduct() {
		List<ProductVo> productVo = new ArrayList<>();
		try {
			productVo = inventoryService.getAllProduct();
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
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
			productBo = inventoryService.updateProduct(productBo);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productBo);

	}

	@DeleteMapping("/delete-product/{id}")
	public ResponseEntity<Boolean> deleteProduct(@PathVariable("id") long productId) {
		boolean status = false;
		try {
			status = inventoryService.deleteProduct(productId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("/get-product/{productId}")
	public ResponseEntity<?> getSingleProduct(@PathVariable("productId") long productId) {
		ProductVo productVo = new ProductVo();
		try {

			productVo = inventoryService.getSingleProduct(productId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(productVo);

	}

	@GetMapping("list-product/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listProducts(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			Page<ProductBo> listPage = inventoryService.listProduct(pageIndex, pageSize, columnName, sortOrder,
					searchText);
			if (listPage != null) {
				return ResponseEntity.ok(listPage);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	/*
	 * @PutMapping("/change-product-owner") public ResponseEntity<?>
	 * changeProductOwner(
	 * 
	 * @RequestBody ProductBo productBo,HttpServletRequest request){
	 * 
	 * try { if(null!=request.getHeader("id")) { String ids=request.getHeader("id");
	 * int number = Integer.parseInt(ids); productBo.setModifiedBy(number);
	 * productBo.setModified(new Date()); }
	 * 
	 * productBo = inventoryService.changeProductOwner(productBo); }catch (Exception
	 * e) { if(log.isDebugEnabled()) { log.info(e.getMessage(),e); } } return
	 * ResponseEntity.ok(productBo);
	 * 
	 * }
	 */

	@GetMapping("/export-product")
	public void exportProduct(HttpServletResponse response) throws IOException {
		// Set response headers
		List<ProductVo> productVo = new ArrayList<>();

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=leads.csv");

		// Get product data from service
		try {
			productVo = inventoryService.getAllProduct();
			System.out.println(productVo);

			// Write CSV data to response
			CSVExpoter.exportProductToCSV(response.getWriter(), productVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/import-product")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws CsvValidationException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
		}

		try (InputStreamReader reader = new InputStreamReader(file.getInputStream());

				CSVReader csvReader = new CSVReader(reader)) {

			String[] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (nextRecord.length > 0) {
					ProductVo entity = new ProductVo();
					// entity.setProductId(Integer.parseInt(nextRecord[0]));
					entity.setProductName(nextRecord[0]);
					entity.setProductType(nextRecord[5]);
					entity.setStartDate(nextRecord[6]);
					entity.setEndDate(nextRecord[7]);
					entity.setMinStocks(Long.parseLong(nextRecord[2]));
					entity.setMaxStocks(Long.parseLong(nextRecord[3]));
					entity.setAvaliablesStocks(Long.parseLong(nextRecord[4]));
					entity.setProductSpecification(nextRecord[1]);
					productRepository.save(entity);
				} else {
					return ResponseEntity.badRequest().body("Invalid CSV format");
				}
			}

			return ResponseEntity.ok(true);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	// ***************************************** PRICE BOOK
	// **********************************************

	@GetMapping("/check-duplicate-price-book")
	public ResponseEntity<?> isDuplicatePriceBook(@RequestParam("priceBookName") String priceBookName) {
		try {
			Boolean isDuplicate = inventoryService.isDuplicatePriceBook(priceBookName);
			if (isDuplicate != null) {
				return ResponseEntity.ok(isDuplicate);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/create-priceBook")
	public ResponseEntity<?> createPriceBook(@RequestBody PriceBookBO priceBookBo, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				int number = Integer.parseInt(id);
				priceBookBo.setCreatedBy(number);
				priceBookBo.setCreatedTime(new Date());

			}
			priceBookBo = inventoryService.createPriceBook(priceBookBo);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(priceBookBo);

	}

	@GetMapping("/get-priceBook")
	public ResponseEntity<?> getAllPriceBook() {
		List<PriceBookVO> priceBookVO = new ArrayList<>();
		try {
			priceBookVO = inventoryService.getAllPriceBook();
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}

		return ResponseEntity.ok(priceBookVO);

	}

	@GetMapping("/get-priceBookById/{priceBookId}")
	public ResponseEntity<?> getPriceBookByIds(@PathVariable("priceBookId") int priceBookId) {
		PriceBookBO priceBookBO = new PriceBookBO();
		try {
			priceBookBO = inventoryService.getPriceBookById(priceBookId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(priceBookBO);

	}

	@PutMapping("/update-priceBook")
	public ResponseEntity<?> updatePriceBook(@RequestBody PriceBookBO priceBookBO, HttpServletRequest request) {
		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				int number = Integer.parseInt(id);
				priceBookBO.setModifyiedBy(number);
				priceBookBO.setModifiedTime(new Date());

			}
			priceBookBO = inventoryService.updatePriceBook(priceBookBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(priceBookBO);

	}

	@DeleteMapping("/delete-priceBook/{priceBookId}")
	public ResponseEntity<Boolean> deletePriceBook(@PathVariable("priceBookId") int priceBookId) {
		boolean status = false;
		try {
			status = inventoryService.deletePriceBook(priceBookId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("list-pricebook/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listPriceBooks(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			Page<PriceBookBO> listPage = inventoryService.listPriceBook(pageIndex, pageSize, columnName, sortOrder,
					searchText);
			if (listPage != null) {
				return ResponseEntity.ok(listPage);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PutMapping("/change-pricebook-owner")
	public ResponseEntity<?> changePriceBook(@RequestBody PriceBookBO priceBookBO, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String ids = request.getHeader("id");
				int number = Integer.parseInt(ids);
				priceBookBO.setModifyiedBy(number);
				priceBookBO.setModifiedTime(new Date());
			}

			priceBookBO = inventoryService.changePriceBookOwner(priceBookBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(priceBookBO);

	}

	// ***************************************** SUPPLIER
	// **********************************************

	@GetMapping("/check-duplicate-supplier")
	public ResponseEntity<?> isDuplicateSupplier(@RequestParam("supplierName") String supplierName) {
		try {
			Boolean isDuplicate = inventoryService.isDuplicateSupplier(supplierName);
			if (isDuplicate != null) {
				return ResponseEntity.ok(isDuplicate);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/create-supplier")
	public ResponseEntity<?> createSupplier(@RequestBody SupplierBO supplierBO, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				supplierBO.setCreatedBy(number);
			}
			supplierBO = inventoryService.createSupplier(supplierBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(supplierBO);

	}

	@ReadOperation
	@GetMapping("/get-all-supplier")
	public ResponseEntity<?> getAllSupplier() {
		List<SupplierVO> supplierVO = new ArrayList<>();
		try {
			supplierVO = inventoryService.getAllSupplier();
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}

		return ResponseEntity.ok(supplierVO);

	}

	@PutMapping("/update-supplier")
	public ResponseEntity<?> updateSupplier(@RequestBody SupplierBO supplierBO, HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				supplierBO.setModifiedBy(number);
			}
			supplierBO = inventoryService.updateSupplier(supplierBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(supplierBO);

	}

	@DeleteMapping("/delete-supplier/{id}")
	public ResponseEntity<Boolean> deleteSupplier(@PathVariable("id") long supplierId) {
		boolean status = false;
		try {
			status = inventoryService.deleteSupplier(supplierId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("/get-supplier/{supplierId}")
	public ResponseEntity<?> getSingleSupplier(@PathVariable("supplierId") long supplierId) {
		SupplierVO supplierVO = new SupplierVO();
		try {

			supplierVO = inventoryService.getSingleSupplier(supplierId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(supplierVO);

	}

	@GetMapping("list-supplier/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listSuppliers(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			Page<SupplierBO> listPage = inventoryService.listSupplier(pageIndex, pageSize, columnName, sortOrder,
					searchText);
			if (listPage != null) {
				return ResponseEntity.ok(listPage);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	/*
	 * @PutMapping("/change-supplier-owner") public ResponseEntity<?>
	 * changeSupplierOwner(
	 * 
	 * @RequestBody SupplierBO supplierBO,HttpServletRequest request){
	 * 
	 * try { if(null!=request.getHeader("id")) { String ids=request.getHeader("id");
	 * int number = Integer.parseInt(ids); supplierBO.setModifiedBy(number);
	 * supplierBO.setModified(new Date()); }
	 * 
	 * supplierBO = inventoryService.changeSupplierOwner(supplierBO); }catch
	 * (Exception e) { if(log.isDebugEnabled()) { log.info(e.getMessage(),e); } }
	 * return ResponseEntity.ok(supplierBO);
	 * 
	 * }
	 */

	@GetMapping("/export-supplier")
	public void exportSupplier(HttpServletResponse response) throws IOException {
		// Set response headers
		List<SupplierVO> supplierVO = new ArrayList<>();

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=leads.csv");

		// Get supplier data from service
		try {
			supplierVO = inventoryService.getAllSupplier();
			System.out.println(supplierVO);

			// Write CSV data to response
			CSVExpoter.exportSupplierToCSV(response.getWriter(), supplierVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostMapping("/import-supplier")
	public ResponseEntity<?> uploadFileSupplier(@RequestParam("file") MultipartFile file)
			throws CsvValidationException, ParseException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
		}

		try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
				CSVReader csvReader = new CSVReader(reader)) {

			String[] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (nextRecord.length > 0) {
					SupplierVO entity = new SupplierVO();
					// entity.setSupplierId(Integer.parseInt(nextRecord[0]));
					entity.setSupplierName(nextRecord[1]);
					entity.setEmailId(nextRecord[2]);
					entity.setMobileNo(Long.parseLong(nextRecord[3]));
					entity.setAddress(nextRecord[4]);
					entity.setCity(nextRecord[5]);
					entity.setState(nextRecord[6]);
					entity.setCountry(nextRecord[7]);
					entity.setWebsite(nextRecord[8]);
					entity.setTechOriented(nextRecord[9]);
					entity.setFinancialAmount(Double.parseDouble(nextRecord[10]));
					entity.setRating(Double.parseDouble(nextRecord[11]));
					entity.setLocation(nextRecord[12]);
					entity.setCreatedBy(Integer.parseInt(nextRecord[13]));
					entity.setModifyiedBy(Integer.parseInt(nextRecord[14]));
					supplierRepository.save(entity);
				} else {
					return ResponseEntity.badRequest().body("Invalid CSV format");
				}
			}

			return ResponseEntity.ok(true);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	// ***************************************** PURCHASE ORDER
	// **********************************************
	@PostMapping("/create-purchaseOrder")
	public ResponseEntity<?> createPurchaseOrder(@RequestBody PurchaseOrderBO purchaseOrderBO,
			HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				purchaseOrderBO.setCreatedBy(number);
			}
			purchaseOrderBO = inventoryService.createPurchaseOrder(purchaseOrderBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(purchaseOrderBO);

	}

	@ReadOperation
	@GetMapping("/get-all-purchaseOrder")
	public ResponseEntity<?> getAllPurchaseOrder() {
		List<PurchaseOrderVO> purchaseOrderVO = new ArrayList<>();
		try {
			purchaseOrderVO = inventoryService.getAllPurchaseOrder();
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}

		return ResponseEntity.ok(purchaseOrderVO);

	}

	@PutMapping("/update-purchaseOrder")
	public ResponseEntity<?> updatePurchaseOrder(@RequestBody PurchaseOrderBO purchaseOrderBO,
			HttpServletRequest request) {

		try {
			if (null != request.getHeader("id")) {
				String id = request.getHeader("id");
				long number = Long.parseLong(id);
				purchaseOrderBO.setModifiedBy(number);
			}
			purchaseOrderBO = inventoryService.updatePurchaseOrder(purchaseOrderBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(purchaseOrderBO);

	}

	@DeleteMapping("/delete-productOrder/{id}")
	public ResponseEntity<Boolean> deletePurchaseOrder(@PathVariable("id") long purchaseOrderId) {
		boolean status = false;
		try {
			status = inventoryService.deletePurchaseOrder(purchaseOrderId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(status);

	}

	@GetMapping("/get-purchaseOrder/{purchaseOrderId}")
	public ResponseEntity<?> getSinglePurchaseOrder(@PathVariable("purchaseOrderId") long purchaseOrderId) {
		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();
		try {

			purchaseOrderVO = inventoryService.getSinglePurchaseOrder(purchaseOrderId);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return ResponseEntity.ok(purchaseOrderVO);

	}

	@GetMapping("list-purchase-order/{pageIndex}/{pageSize}")
	public ResponseEntity<?> listPurchaseOrders(@PathVariable("pageIndex") int pageIndex,
			@PathVariable("pageSize") int pageSize,
			@RequestParam(name = "columnName", required = false) String columnName,
			@RequestParam(name = "sortOrder", required = false) String sortOrder,
			@RequestParam(name = "searchText", required = false) String searchText) {
		try {
			Page<PurchaseOrderBO> listPage = inventoryService.listPurchaseOrder(pageIndex, pageSize, columnName,
					sortOrder, searchText);
			if (listPage != null) {
				return ResponseEntity.ok(listPage);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@GetMapping("/check-duplicate-subject")
	public ResponseEntity<?> isDuplicateSubject(@RequestParam("subject") String subject) {
		try {
			Boolean isDuplicate = inventoryService.isDuplicateSubject(subject);
			if (isDuplicate != null) {
				return ResponseEntity.ok(isDuplicate);
			} else {
				return ResponseEntity.internalServerError().body("Internal Server Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
	}

	@PostMapping("/get-duplicate-update")
	public ResponseEntity<?> listOfDuplicates(@RequestParam("productId") String productId) {

		try {

		} catch (Exception E) {

		}

		return null;

	}

}
