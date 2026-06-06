package com.microservice.inventory.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
import com.microservice.inventory.repository.PriceBookRepository;
import com.microservice.inventory.repository.ProductRepository;
import com.microservice.inventory.repository.ProductTypeRepository;
import com.microservice.inventory.repository.PurchaseOrderRepository;
import com.microservice.inventory.repository.SupplierRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private ProductTypeRepository productTypeRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PriceBookRepository priceBookRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	//***************************************** PRODUCT TYPE **********************************************
	@Override
	public ProductTypeBo createProductType(ProductTypeBo productTypeBo) {
		ProductTypeVo productTypeVo = new ProductTypeVo();
		try {

			BeanUtils.copyProperties(productTypeBo, productTypeVo);
			productTypeVo.setDelete(false);
			productTypeVo.setActive(true);
			productTypeVo = productTypeRepository.save(productTypeVo);
			BeanUtils.copyProperties(productTypeVo, productTypeBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productTypeBo;
	}

	@Override
	public List<ProductTypeVo> getAllProductType() {
		List<ProductTypeVo> productTypeVo = new ArrayList<>();
		try {
			productTypeVo = productTypeRepository.getAllProductType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productTypeVo;
	}

	@Override
	public ProductTypeBo updateProductType(ProductTypeBo productTypeBo) {
		ProductTypeVo productTypeVo = new ProductTypeVo();
		try {
			BeanUtils.copyProperties(productTypeBo, productTypeVo);
			productTypeVo = productTypeRepository.save(productTypeVo);
			productTypeBo.setProductTypeId(productTypeVo.getProductTypeId());
			BeanUtils.copyProperties(productTypeVo, productTypeBo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productTypeBo;
	}

	@Override
	public boolean deleteProductType(long productTypeId) {
		try {
			productTypeRepository.deleteProductType(productTypeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	@Override
	public ProductTypeVo getSingleProductType(long productTypeId) {
		ProductTypeVo productTypeVo = new ProductTypeVo();
		try {
			Optional<ProductTypeVo> productType = productTypeRepository.findById(productTypeId);
			productTypeVo = productType.get();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return productTypeVo;
	}
	
	@Override
	public Page<ProductTypeBo> listProductType(int pageIndex, int pageSize,String sortOrder,
			String searchText) {
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by("productType").ascending();
				} else {
					sort = Sort.by("productType").descending();
				}
			} else {
				sort = Sort.by("productTypeId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<ProductTypeVo> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = productTypeRepository.findAllByProductTypeContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				pageVo = productTypeRepository.findAllByIsDelete(false, pageable);
			}
			List<ProductTypeVo> listVo = pageVo.getContent();
			List<ProductTypeBo> listBo = new ArrayList<>();
			if (listVo != null) {
				for (ProductTypeVo vo : listVo) {
					ProductTypeBo bo = new ProductTypeBo();
					BeanUtils.copyProperties(vo, bo);
					listBo.add(bo);
				}
			}
			return new PageImpl<ProductTypeBo>(listBo, pageable, pageVo.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}


	//***************************************** PRODUCT **********************************************
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
	
	@Override
	public Page<ProductBo> listProduct(int pageIndex, int pageSize, String columnName, String sortOrder,
			String searchText) {
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by(columnName).ascending();
				} else {
					sort = Sort.by(columnName).descending();
				}
			} else {
				sort = Sort.by("productId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<ProductVo> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = productRepository.findAllByProductNameContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				pageVo = productRepository.findAllByIsDelete(false, pageable);
			}
			List<ProductVo> listVo = pageVo.getContent();
			List<ProductBo> listBo = new ArrayList<>();
			if (listVo != null) {
				for (ProductVo vo : listVo) {
					ProductBo bo = new ProductBo();
					BeanUtils.copyProperties(vo, bo);
					listBo.add(bo);
				}
			}
			return new PageImpl<ProductBo>(listBo, pageable, pageVo.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public ProductBo changeProductOwner(ProductBo productBo) {
		ProductVo productVo = new ProductVo();
		try {
			BeanUtils.copyProperties(productBo, productVo);
			productVo = productRepository.save(productVo);
			productBo.setProductId(productVo.getProductId());
			BeanUtils.copyProperties(productVo, productBo);
		}catch (Exception e) {
			if(log.isDebugEnabled()) {
                log.info(e.getMessage(),e);
            }
		}
		return productBo;
	}

	//***************************************** PRICE BOOK **********************************************
	@Override
	public boolean deletePriceBook(int priceBookId) {
		priceBookRepository.priceBookDeleteById(priceBookId);
		return true;
	}

	@Override
	public PriceBookBO updatePriceBook(PriceBookBO priceBookBO) {
		PriceBookVO priceBookVO = new PriceBookVO();
		try {
			BeanUtils.copyProperties(priceBookBO, priceBookVO);
			priceBookVO = priceBookRepository.save(priceBookVO);
			priceBookVO.setPriceBookId(priceBookVO.getPriceBookId());
			BeanUtils.copyProperties(priceBookVO, priceBookVO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return priceBookBO;
	}

	@Override
	public List<PriceBookVO> getAllPriceBook() {
		List<PriceBookVO> priceBookVO = new ArrayList<>();
		try {
			priceBookVO = priceBookRepository.priceBookfindAll();
//			Object object=restTemplate.getForObject("http://product-service/product/get-single-id/{productId}", Object.class);

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return priceBookVO;
	}

	@Override
	public PriceBookBO createPriceBook(PriceBookBO priceBookBO) {
		PriceBookVO priceBookVO = new PriceBookVO();
		try {
			BeanUtils.copyProperties(priceBookBO, priceBookVO);
			priceBookVO = priceBookRepository.save(priceBookVO);
			BeanUtils.copyProperties(priceBookVO, priceBookBO);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return priceBookBO;
	}

	@Override
	public PriceBookBO getPriceBookById(int priceBookId) {
		PriceBookBO priceBookBO = new PriceBookBO();
		PriceBookVO priceBookVO = new PriceBookVO();
		try {
			priceBookVO = priceBookRepository.getById(priceBookId);
			BeanUtils.copyProperties(priceBookVO, priceBookBO);
		} catch (Exception e) {

			if (log.isDebugEnabled()) {
				log.info(e.getMessage(), e);
			}
		}
		return priceBookBO;
	}
	
	@Override
	public Page<PriceBookBO> listPriceBook(int pageIndex, int pageSize, String columnName, String sortOrder,
			String searchText) {
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by(columnName).ascending();
				} else {
					sort = Sort.by(columnName).descending();
				}
			} else {
				sort = Sort.by("productId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<PriceBookVO> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = priceBookRepository.findAllByPriceBookNameContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				pageVo = priceBookRepository.findAllByIsDelete(false, pageable);
			}
			List<PriceBookVO> listVo = pageVo.getContent();
			List<PriceBookBO> listBo = new ArrayList<>();
			if (listVo != null) {
				for (PriceBookVO vo : listVo) {
					PriceBookBO bo = new PriceBookBO();
					BeanUtils.copyProperties(vo, bo);
					listBo.add(bo);
				}
			}
			return new PageImpl<PriceBookBO>(listBo, pageable, pageVo.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public PriceBookBO changePriceBookOwner(PriceBookBO priceBookBO) {
		PriceBookVO priceBookVO = new PriceBookVO();
		try {
			BeanUtils.copyProperties(priceBookBO, priceBookVO);
			priceBookVO = priceBookRepository.save(priceBookVO);
			priceBookBO.setProductId(priceBookVO.getProductId());
			BeanUtils.copyProperties(priceBookVO, priceBookBO);
		}catch (Exception e) {
			if(log.isDebugEnabled()) {
                log.info(e.getMessage(),e);
            }
		}
		return priceBookBO;
	}

	//***************************************** SUPPLIER **********************************************
	@Override
	public SupplierBO createSupplier(SupplierBO supplierBO) {
		SupplierVO supplierVO = new SupplierVO();
		try {
			BeanUtils.copyProperties(supplierBO, supplierVO);
			supplierVO.setDelete(false);
			supplierVO.setActive(true);
			supplierVO = supplierRepository.save(supplierVO);
			BeanUtils.copyProperties(supplierVO, supplierBO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplierBO;
	}

	@Override
	public List<SupplierVO> getAllSupplier() {
		List<SupplierVO> supplierVO = new ArrayList<>();
		try {
			supplierVO = supplierRepository.getAllSupplier();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplierVO;
	}

	@Override
	public SupplierBO updateSupplier(SupplierBO supplierBO) {
		SupplierVO supplierVO = new SupplierVO();
		try {
			BeanUtils.copyProperties(supplierBO, supplierVO);
			supplierVO = supplierRepository.save(supplierVO);
			supplierBO.setSupplierId(supplierVO.getSupplierId());
			BeanUtils.copyProperties(supplierVO, supplierBO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplierBO;
	}

	@Override
	public boolean deleteSupplier(long supplierId) {
		try {
			supplierRepository.deleteSupplier(supplierId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public SupplierVO getSingleSupplier(long supplierId) {
		SupplierVO supplierVO = new SupplierVO();
		try {
			Optional<SupplierVO> supplier = supplierRepository.findById(supplierId);
			supplierVO = supplier.get();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplierVO;
	}
	
	@Override
	public Page<SupplierBO> listSupplier(int pageIndex, int pageSize, String columnName, String sortOrder,
			String searchText) {
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by(columnName).ascending();
				} else {
					sort = Sort.by(columnName).descending();
				}
			} else {
				sort = Sort.by("supplierId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<SupplierVO> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = supplierRepository.findAllBySupplierNameContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				pageVo = supplierRepository.findAllByIsDelete(false, pageable);
			}
			List<SupplierVO> listVo = pageVo.getContent();
			List<SupplierBO> listBo = new ArrayList<>();
			if (listVo != null) {
				for (SupplierVO vo : listVo) {
					SupplierBO bo = new SupplierBO();
					BeanUtils.copyProperties(vo, bo);
					listBo.add(bo);
				}
			}
			return new PageImpl<SupplierBO>(listBo, pageable, pageVo.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	@Override
	public SupplierBO changeSupplierOwner(SupplierBO supplierBO) {
		SupplierVO supplierVO = new SupplierVO();
		try {
			BeanUtils.copyProperties(supplierBO, supplierVO);
			supplierVO = supplierRepository.save(supplierVO);
			supplierBO.setSupplierId(supplierVO.getSupplierId());
			BeanUtils.copyProperties(supplierVO, supplierBO);
		}catch (Exception e) {
			if(log.isDebugEnabled()) {
                log.info(e.getMessage(),e);
            }
		}
		return supplierBO;
	}
	

	//***************************************** PURCHASE ORDER **********************************************
	@Override
	public PurchaseOrderBO createPurchaseOrder(PurchaseOrderBO purchaseOrderBO) {
		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();
		try {

			BeanUtils.copyProperties(purchaseOrderBO, purchaseOrderVO);
			purchaseOrderVO.setDelete(false);
			purchaseOrderVO.setActive(true);
			purchaseOrderVO.setCreated(new Date());
			purchaseOrderVO = purchaseOrderRepository.save(purchaseOrderVO);
			BeanUtils.copyProperties(purchaseOrderVO, purchaseOrderBO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseOrderBO;
	}

	@Override
	public List<PurchaseOrderVO> getAllPurchaseOrder() {
		List<PurchaseOrderVO> purchaseOrderVO = new ArrayList<>();
		try {
			purchaseOrderVO = purchaseOrderRepository.getAllPurchaseOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseOrderVO;
	}

	@Override
	public PurchaseOrderBO updatePurchaseOrder(PurchaseOrderBO purchaseOrderBO) {
		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();
		try {
			BeanUtils.copyProperties(purchaseOrderBO, purchaseOrderVO);
			purchaseOrderVO.setModified(new Date());
			purchaseOrderVO = purchaseOrderRepository.save(purchaseOrderVO);
			purchaseOrderBO.setPurchaseOrderId(purchaseOrderVO.getPurchaseOrderId());
			BeanUtils.copyProperties(purchaseOrderVO, purchaseOrderBO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseOrderBO;
	}

	@Override
	public boolean deletePurchaseOrder(long purchaseOrderId) {
		try {
			purchaseOrderRepository.deletePurchaseOrder(purchaseOrderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	@Override
	public PurchaseOrderVO getSinglePurchaseOrder(long purchaseOrderId) {
		PurchaseOrderVO purchaseOrderVO = new PurchaseOrderVO();
		try {
			Optional<PurchaseOrderVO> purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId);
			purchaseOrderVO = purchaseOrder.get();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return purchaseOrderVO;
	}


	@Override
	public Page<PurchaseOrderBO> listPurchaseOrder(int pageIndex, int pageSize, String columnName, String sortOrder,
			String searchText) {
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by(columnName).ascending();
				} else {
					sort = Sort.by(columnName).descending();
				}
			} else {
				sort = Sort.by("purchaseOrderId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<PurchaseOrderVO> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = purchaseOrderRepository.findAllBySubjectContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				pageVo = purchaseOrderRepository.findAllByIsDelete(false, pageable);
			}
			List<PurchaseOrderVO> listVo = pageVo.getContent();
			List<PurchaseOrderBO> listBo = new ArrayList<>();
			if (listVo != null) {
				for (PurchaseOrderVO vo : listVo) {
					PurchaseOrderBO bo = new PurchaseOrderBO();
					BeanUtils.copyProperties(vo, bo);
					listBo.add(bo);
				}
			}
			return new PageImpl<PurchaseOrderBO>(listBo, pageable, pageVo.getTotalElements());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	@Override
	public Boolean isDuplicateSubject(String subject) {
		try {
			PurchaseOrderVO purchaseOrder = purchaseOrderRepository.findBySubjectIgnoreCaseAndIsDelete(subject, false);
			if(purchaseOrder!=null) {
				return true ;
			} else {
				return false;
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

	}

	@Override
	public Boolean isDuplicateProductType(String productType) {
		try {
			List<ProductTypeVo> productTypeList = productTypeRepository.findAllByProductTypeIgnoreCaseAndIsDelete(productType, false);
			if(productTypeList!=null && productTypeList.size()>0 ) {
				return true ;
			} else {
				return false;
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	@Override
	public Boolean isDuplicateProduct(String productName) {
		try {
			List<ProductVo> productList = productRepository.findAllByProductNameIgnoreCaseAndIsDelete(productName, false);
			if(productList!=null && productList.size()>0 ) {
				return true ;
			} else {
				return false;
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	@Override
	public Boolean isDuplicatePriceBook(String priceBookName) {
		try {
			List<PriceBookVO> priceBookList = priceBookRepository.findAllByPriceBookNameIgnoreCaseAndIsDelete(priceBookName, false);
			if(priceBookList!=null && priceBookList.size()>0 ) {
				return true ;
			} else {
				return false;
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	@Override
	public Boolean isDuplicateSupplier(String supplierName) {
		try {
			List<SupplierVO> supplierList = supplierRepository.findAllBySupplierNameIgnoreCaseAndIsDelete(supplierName, false);
			if(supplierList!=null && supplierList.size()>0 ) {
				return true ;
			} else {
				return false;
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}

	
	

	
	
	
	

}
