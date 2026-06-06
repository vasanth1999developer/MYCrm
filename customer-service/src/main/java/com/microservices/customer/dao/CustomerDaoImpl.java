package com.microservices.customer.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.microservices.customer.entity.CustomerVo;

import lombok.extern.log4j.Log4j2;


@Repository
@Log4j2
public class CustomerDaoImpl implements CustomerDao{


	@Autowired
	private EntityManager entityManager;

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public long createCustomer(CustomerVo CustomerVo) throws Exception {
		long CustomerId = 0;
		try {
			entityManager.persist(CustomerVo);
			if (CustomerVo.getCustomerId() > 0) {
				CustomerId = CustomerVo.getCustomerId();
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return CustomerId;
	}
	@Override
	public List<CustomerVo> viewCustomer(CustomerVo CustomerVo) throws Exception {
		List<CustomerVo> CustomerVoList = null;
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerVo> criteriaQuery = criteriaBuilder.createQuery(CustomerVo.class);
			Root<CustomerVo> root = criteriaQuery.from(CustomerVo.class);
			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("isDelete"), false));
			Query finalQuery = (Query) entityManager.createQuery(criteriaQuery);
			CustomerVoList = finalQuery.getResultList();
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return CustomerVoList;
	}
	@Override
	public CustomerVo retrieveCustomerById(CustomerVo CustomerVo) throws Exception {
		try {

			CustomerVo= ((EntityManager) entityManager).find(CustomerVo.class,CustomerVo.getCustomerId());
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return CustomerVo;
	}

	@Override
	public boolean updateCustomer(CustomerVo CustomerVo) throws Exception {
		boolean status = false;
		try {
			entityManager.merge(CustomerVo);
			if (null != CustomerVo && CustomerVo.getCustomerId() > 0) {
				status = true;
			}
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return status;
	}

	@Override
	public CustomerVo deleteCustomer(CustomerVo CustomerVo) throws Exception {
		int result = 0;
		String deletequery = "UPDATE CustomerVo f set f.isDelete=:isDelete where f.customerId=:customerId";
		try {
			Query query = entityManager.createQuery(deletequery);
			query.setParameter("isDelete", CustomerVo.isDelete());
			query.setParameter("customerId", CustomerVo.getCustomerId());
			result = query.executeUpdate();
		} catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;
	}
	@Override
	public boolean findByParam(String entity, String emailAddress) {

		boolean customerExists = false;		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerVo> criteriaQuery = criteriaBuilder.createQuery(CustomerVo.class);
			Root<CustomerVo> root = criteriaQuery.from(CustomerVo.class);


			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("emailId"), emailAddress),
					criteriaBuilder.equal(root.get("isDelete"), false));
			TypedQuery<CustomerVo> query = entityManager.createQuery(criteriaQuery);


			CustomerVo customer = query.getSingleResult();
			if (customer != null && customer.getCustomerId() > 0) {
				customerExists = true;
			}

		}
		catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}

		return customerExists;
	}

	@Override
	public boolean findByMobileNo(String entity, String mobileNo) {

		boolean customerExists = false;		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerVo> criteriaQuery = criteriaBuilder.createQuery(CustomerVo.class);
			Root<CustomerVo> root = criteriaQuery.from(CustomerVo.class);


			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("mobileNo"), mobileNo),
					criteriaBuilder.equal(root.get("isDelete"), false));
			TypedQuery<CustomerVo> query = entityManager.createQuery(criteriaQuery);


			CustomerVo customer = query.getSingleResult();
			if (customer != null && customer.getCustomerId() > 0) {
				customerExists = true;
			}

		}
		catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}

		return customerExists;
	}


	private long countRecord() {
		long count = 0;
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
			Root<CustomerVo> root = countQuery.from(CustomerVo.class);
			countQuery.where(criteriaBuilder.equal(root.get("isDelete"), false));
			countQuery.select(criteriaBuilder.count(root));
			count= entityManager.createQuery(countQuery).getSingleResult();

		}catch(Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return count;
	}
	@Override
	public Page<CustomerVo> findPaginated(PageRequest pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerVo> criteriaQuery = criteriaBuilder.createQuery(CustomerVo.class);
			Root<CustomerVo> root = criteriaQuery.from(CustomerVo.class);
			CriteriaQuery<CustomerVo> select = criteriaQuery.select(root). where(criteriaBuilder.equal(root.get("isDelete"), false));
			TypedQuery<CustomerVo> typedQuery = entityManager.createQuery(select);
			typedQuery.setFirstResult((int) pageable.getOffset());
			typedQuery.setMaxResults(pageable.getPageSize());
			List<CustomerVo> itemList = typedQuery.getResultList();
			return new PageImpl<>(itemList, pageable, countRecord());

		}catch (Exception e) {
			if(log.isDebugEnabled()) {
				log.info(e.getMessage(),e);
			}
		}
		return null;

	}
}
