package com.clinch.crm.authorization.service;

import java.util.*;
import java.util.stream.Collectors;

import com.clinch.crm.authorization.common.AccessSpecification;
import com.clinch.crm.authorization.common.CursorResponse;
import com.clinch.crm.authorization.common.PaginatedResponse;
import com.clinch.crm.authorization.customexception.DuplicateResourceException;
import com.clinch.crm.authorization.customexception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.clinch.crm.authorization.entity.AccessVo;
import com.clinch.crm.authorization.model.AccessBo;
import com.clinch.crm.authorization.repository.AccessRepository;

import java.util.List;

/**
 * Default {@link AccessService}.
 *
 * <p>Newer than the role/privilege services: create, update and list throw exceptions that
 * {@code GlobalExceptionHandler} turns into HTTP errors, instead of returning {@code false}.
 */
@Service
public class AccessServiceImpl implements AccessService {


	/** Access table access. */
	@Autowired
	AccessRepository accessRepository;


	/** {@inheritDoc} */
	@Override
	public boolean isDuplicateAccess(String accessName) {

		boolean exists = accessRepository
				.existsByAccessNameAndIsDeleted(accessName, false);

		if (exists) {
			throw new DuplicateResourceException("Access already exists");
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public AccessVo createAccess(AccessBo accessBo) {

		if (accessBo == null) {
			throw new IllegalArgumentException("Access data cannot be null");
		}

		if (accessRepository.existsByAccessNameAndIsDeleted(String.valueOf(accessBo.getAccessName()), false)) {
			throw new DuplicateResourceException("Access already exists....");
		}

		AccessVo accessVo = new AccessVo();
		BeanUtils.copyProperties(accessBo, accessVo);

		return accessRepository.save(accessVo);
	}

	/** {@inheritDoc} Soft-deleted entries are returned too. */
	@Override
	public AccessBo getAccessByAccessId(int accessId) {
		AccessBo accessBo = null;
		try {

			AccessVo accessVo = accessRepository.findById(accessId)
					.orElse(null);
			if (accessVo != null) {
				accessBo = new AccessBo();
				BeanUtils.copyProperties(accessVo, accessBo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessBo;
	}



	/**
	 * {@inheritDoc}
	 *
	 * <p>Instead of "skip N rows" ({@code OFFSET}), it asks for "rows with id greater than
	 * {@code lastId}", which stays fast on big tables. A blank {@code searchText} is treated as no
	 * filter. {@code nextCursor} is the last id of this page, or {@code null} when there are no more.
	 */
	@Override
	public CursorResponse<AccessBo> listAccesses(Integer lastId, int size, String searchText) {


		if (searchText != null && searchText.trim().isEmpty()) {
			searchText = null;
		}


		size = Math.min(size, 50);

		Pageable pageable = PageRequest.of(0, size);


		Slice<AccessVo> slice =
				accessRepository.findAccessesWithCursor(lastId, searchText, pageable);

		List<AccessVo> listVo = slice.getContent();


		if (listVo.isEmpty()) {
			return new CursorResponse<>(
					Collections.emptyList(),
					null,
					false,
					size
			);
		}


		List<AccessBo> data = listVo.stream()
				.map(vo -> new AccessBo(vo.getAccessId(), vo.getAccessName()))
				.collect(Collectors.toList());


		Integer nextCursor = listVo.get(listVo.size() - 1).getAccessId();


		if (!slice.hasNext()) {
			nextCursor = null;
		}

		return new CursorResponse<>(
				data,
				nextCursor,
				slice.hasNext(),
				size
		);
	}

	/** Fields {@link #getAccess} may sort by; protects against sorting on arbitrary properties. */
	private static final List<String> ALLOWED_SORT_FIELDS =
			List.of("accessId", "accessName");


	/**
	 * {@inheritDoc}
	 *
	 * <p>Clamps {@code page} to 0 or more and {@code size} to 1-100. The filters are built from
	 * {@link AccessSpecification} ("not deleted" AND optional "name contains").
	 *
	 * <p>An unknown {@code sortBy} is replaced with {@code "id"}, which isn't a property of
	 * {@link AccessVo}, so the query fails and the client gets 500.
	 */
	@Override
	public PaginatedResponse<AccessBo> getAccess(int page, int size, String searchText, String sortBy, String direction) {



		if (page < 0) page = 0;
		if (size < 1) size = 1;
		if (size > 100) size = 100;
		if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "id";


		Sort sort = direction.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();


		Pageable pageable = PageRequest.of(page, size, sort);


		Specification<AccessVo> spec = Specification
				.where(AccessSpecification.notDeleted())
				.and(AccessSpecification.searchText(searchText));


		Page<AccessVo> result = accessRepository.findAll(spec, pageable);



		return PaginatedResponse.from(
				result.map(vo -> {
					AccessBo bo = new AccessBo();
					BeanUtils.copyProperties(vo, bo);
					return bo;
				})
		);


	}


	/**
	 * {@inheritDoc}
	 *
	 * <p>Only {@code accessName} is changed; the name isn't checked for duplicates.
	 */
	@Override
	public boolean updateAccess(AccessBo accessBo) {
		if (accessBo == null || accessBo.getAccessId() <= 0) {
			return false;
		}

		AccessVo existing = accessRepository.findById(accessBo.getAccessId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Access not found: " + accessBo.getAccessId()));

		existing.setAccessName(accessBo.getAccessName());
		accessRepository.save(existing);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>A missing entry causes a caught {@code NullPointerException} ({@code false}). Privileges
	 * that link to the entry keep the link.
	 */
	@Override
	public boolean deleteAccess(int accessId) {
		boolean softDeletionStatus = false;
		try {
			if (accessId > 0) {
				AccessVo accessVo = accessRepository.findById(accessId).orElse(null);
				accessVo.setDeleted(true);
				accessVo = accessRepository.save(accessVo);
				if ((accessVo != null) && accessVo.getAccessId() > 0 && accessVo.isDeleted()) {
					softDeletionStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return softDeletionStatus;
	}

	/** {@inheritDoc} */
	@Override
	public Set<AccessBo> listAllAccesses() {

		Set<AccessBo> setBo = null ;

		try {
			Set<AccessVo> setVo = accessRepository.findAllByIsDeleted(false);
			if((setVo !=null) &&(!setVo.isEmpty())) {
				setBo = new TreeSet<>(Comparator.comparing(AccessBo::getAccessName));
				for(AccessVo accessVo : setVo) {
					AccessBo accessBo = new AccessBo();
					BeanUtils.copyProperties(accessVo, accessBo);
					setBo.add(accessBo);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return setBo;

	}

}
