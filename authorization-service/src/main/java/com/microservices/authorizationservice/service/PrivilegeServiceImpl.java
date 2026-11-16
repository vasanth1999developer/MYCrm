package com.microservices.authorizationservice.service;

import java.util.*;
import java.util.stream.Collectors;

import com.microservices.authorizationservice.entity.RoleVo;
import com.microservices.authorizationservice.repository.AccessRepository;
import com.microservices.authorizationservice.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.microservices.authorizationservice.entity.AccessVo;
import com.microservices.authorizationservice.entity.PrivilegeVo;
import com.microservices.authorizationservice.model.AccessBo;
import com.microservices.authorizationservice.model.PrivilegeBo;
import com.microservices.authorizationservice.repository.PrivilegeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Default {@link PrivilegeService}, converting between {@link PrivilegeBo} (API) and
 * {@link PrivilegeVo} (database).
 *
 * <p>As in {@link RoleServiceImpl}, {@link BeanUtils#copyProperties} skips the
 * {@code accesses} sets (different element types), so they are converted by hand. Most methods
 * catch all exceptions and return {@code false}/{@code null}.
 */
@Slf4j
@Service
public class PrivilegeServiceImpl implements PrivilegeService {
	/** Used by {@link #getPrivilegeNamesByRoleNames}. */
	@Autowired
	RoleRepository roleRepository;

	/** Privilege table access. */
	@Autowired
	public PrivilegeRepository repository;

	/** Used to load the real access rows when creating a privilege. */
	@Autowired
	AccessRepository accessRepository;

	/** {@inheritDoc} */
	@Override
	public boolean checkDuplicateName(String privilegeName) {
		boolean status = false;
		try {
			PrivilegeVo privilegeVo = repository.findByPrivilegeNameAndIsDelete(privilegeName, false);
			if (privilegeVo != null) {
				status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Each access is loaded from the database by id, so the request only needs {@code accessId}.
	 * On success the saved id and name are copied back into the given {@code privilegeBo}, which is
	 * returned.
	 */
	@Override
	public PrivilegeBo createPrivilege(PrivilegeBo privilegeBo) {
		try {
			PrivilegeVo privilegeVo = new PrivilegeVo();
			BeanUtils.copyProperties(privilegeBo, privilegeVo);


			Set<AccessVo> setVo = new HashSet<>();          // was TreeSet — no comparator, no NPE
			Set<AccessBo> setBo = privilegeBo.getAccesses();

			if (setBo != null && !setBo.isEmpty()) {
				for (AccessBo accessBo : setBo) {
					// load the real, fully-populated access row instead of a stub
					AccessVo accessVo = accessRepository.findById(accessBo.getAccessId())
							.orElseThrow(() -> new RuntimeException(
									"Access not found: " + accessBo.getAccessId()));
					setVo.add(accessVo);
				}
				privilegeVo.setAccesses(setVo);

				privilegeVo = repository.save(privilegeVo);
				if (privilegeVo != null) {
					BeanUtils.copyProperties(privilegeVo, privilegeBo);
					return privilegeBo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>If {@code sortOrder} is given but {@code searchText} is {@code null}, the
	 * {@code searchText.isEmpty()} call throws a {@code NullPointerException}, which is caught, and
	 * the method returns {@code null}.
	 */
	@Override
	public Page<PrivilegeBo> listAllPrivileges(int pageIndex, int pageSize, String sortOrder, String searchText) {
		Page<PrivilegeVo> privilegeVo = null;
		Page<PrivilegeBo> privilegeBo = null;
		try {
			Sort sort;
			if (sortOrder != null) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by("privilegeName").ascending();
				} else {
					sort = Sort.by("privilegeName").descending();
				}
			} else {
				sort = Sort.unsorted();
			}

			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			if (sortOrder != null && !searchText.isEmpty()) {
				privilegeVo = repository.findAllByPrivilegeNameContainingIgnoreCaseAndIsDelete(searchText, false,
						pageable);
			} else {
				privilegeVo = repository.findAllByIsDelete(false, pageable);
			}
			if (privilegeVo != null) {
				List<PrivilegeVo> privilegeVo1 = privilegeVo.getContent();
				List<PrivilegeBo> privilegeBo1 = new ArrayList<>();
				for (PrivilegeVo privilege : privilegeVo1) {
					PrivilegeBo privilegeBo2 = new PrivilegeBo();
					BeanUtils.copyProperties(privilege, privilegeBo2);
					privilegeBo1.add(privilegeBo2);
				}
				privilegeBo = new PageImpl<PrivilegeBo>(privilegeBo1, pageable, privilegeVo.getTotalElements());
				return privilegeBo;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** {@inheritDoc} Soft-deleted privileges are returned too. */
	@Override
	public PrivilegeBo findIdByPrivilege(int privilegeId) {

		try {
			PrivilegeVo privilegeVo = repository.findById(privilegeId).orElse(null);
			if (privilegeVo != null) {
				PrivilegeBo privilegeBo = new PrivilegeBo();
				BeanUtils.copyProperties(privilegeVo, privilegeBo);
				Set<AccessVo> setVo = privilegeVo.getAccesses();
				Set<AccessBo> setBo = new TreeSet<>(Comparator.comparing(AccessBo::getAccessName));
				if ((setVo != null) && (!setVo.isEmpty())) {
					for (AccessVo accessVo : setVo) {
						AccessBo accessBo = new AccessBo();
						BeanUtils.copyProperties(accessVo, accessBo);
						setBo.add(accessBo);
					}
				}
				privilegeBo.setAccesses(setBo);
				return privilegeBo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The accesses are rebuilt from the request (id and name each); the name is needed because
	 * the set is sorted by it. A missing privilege causes a caught {@code NullPointerException}.
	 */
	@Override
	public boolean updatePrivilege(PrivilegeBo privilegeBo) {
		PrivilegeVo privilegeVo = new PrivilegeVo();
		try {
			privilegeVo = repository.findById(privilegeBo.getPrivilegeId()).orElse(null);
			Set<AccessVo> setVo = new TreeSet<>(Comparator.comparing(AccessVo::getAccessName));
			Set<AccessBo> setBo = privilegeBo.getAccesses();
			if ((setBo != null) && (!setBo.isEmpty())) {
				for (AccessBo accessBo : setBo) {
					AccessVo accessVo = new AccessVo();
					BeanUtils.copyProperties(accessBo, accessVo);
					setVo.add(accessVo);
				}
				privilegeVo.setAccesses(setVo);
				privilegeVo = repository.save(privilegeVo);
				if (privilegeVo != null) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>A missing privilege causes a caught {@code NullPointerException} ({@code false}). The
	 * privilege stays linked to its roles and keeps being granted at sign-in (see
	 * {@link #getPrivilegeNamesByRoleNames}).
	 */
	@Override
	public boolean deletePrivilege(int privilegeId) {
		PrivilegeVo privilegeVo = null;
		try {
			privilegeVo = repository.findById(privilegeId).orElse(null);
			privilegeVo.setDelete(true);
			privilegeVo = repository.save(privilegeVo);
			if (privilegeVo != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Set<PrivilegeBo> listAllPrivileges() {

		Set<PrivilegeBo> setBo = null;

		try {
			Set<PrivilegeVo> setVo = repository.findAllByIsDelete(false);
			if ((setVo != null) && (!setVo.isEmpty())) {
				setBo = new TreeSet<>(Comparator.comparing(PrivilegeBo::getPrivilegeName));
				for (PrivilegeVo privilegeVo : setVo) {
					PrivilegeBo privilegeBo = new PrivilegeBo();
					BeanUtils.copyProperties(privilegeVo, privilegeBo);
					setBo.add(privilegeBo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return setBo;

	}



	/**
	 * {@inheritDoc}
	 *
	 * <p>One query loads the active, non-deleted roles with their privileges
	 * ({@link RoleRepository#findActiveByRoleNames}), then the privilege names are flattened into
	 * a {@link TreeSet} (sorted, no duplicates). Read-only transaction, so the lazy privilege
	 * collections can be read.
	 *
	 * <p>Privileges are <b>not</b> filtered on their own {@code isDelete} flag, so a soft-deleted
	 * privilege is still granted. Each call logs at INFO what was asked and found.
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getPrivilegeNamesByRoleNames(Set<String> roleNames) {
		if (roleNames == null || roleNames.isEmpty()) {
			return Collections.emptySet();
		}

		List<RoleVo> roles = roleRepository.findActiveByRoleNames(roleNames);

		log.info("ASKED FOR: {}", roleNames);
		log.info("ROLES FOUND: {}", roles.size());
		roles.forEach(r -> log.info("  role '{}' -> privileges: {}",
				r.getRoleName(),
				r.getPrivileges() == null ? "NULL" : r.getPrivileges().size()));

		Set<String> result = roles.stream()
				.flatMap(r -> r.getPrivileges().stream())
				.map(PrivilegeVo::getPrivilegeName)
				.collect(Collectors.toCollection(TreeSet::new));

		log.info("RESULT: {}", result.size());
		return result;
	}


}
