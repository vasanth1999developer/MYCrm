package com.microservices.authorizationservice.service;

import java.util.*;
import java.util.stream.Collectors;

import com.microservices.authorizationservice.common.AccessSpecification;
import com.microservices.authorizationservice.common.CursorResponse;
import com.microservices.authorizationservice.common.PaginatedResponse;
import com.microservices.authorizationservice.customexception.DuplicateResourceException;
import com.microservices.authorizationservice.customexception.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.microservices.authorizationservice.entity.AccessVo;
import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.repository.AccessRepository;

import java.util.List;

@Service
public class AccessServiceImpl implements AccessService {


	@Autowired
	AccessRepository accessRepository;


	@Override
	public boolean isDuplicateAccess(String accessName) {

		boolean exists = accessRepository
				.existsByAccessNameAndIsDeleted(accessName, false);

		if (exists) {
			throw new DuplicateResourceException("Access already exists");
		}

		return false;
	}

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

	private static final List<String> ALLOWED_SORT_FIELDS =
			List.of("accessId", "accessName");


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
