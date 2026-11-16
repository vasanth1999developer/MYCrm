package com.clinch.crm.authorization.service;

import java.util.*;

import com.clinch.crm.authorization.repository.PrivilegeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.clinch.crm.authorization.entity.PrivilegeVo;
import com.clinch.crm.authorization.entity.RoleVo;
import com.clinch.crm.authorization.model.PrivilegeBo;
import com.clinch.crm.authorization.model.RoleBo;
import com.clinch.crm.authorization.repository.RoleRepository;

/**
 * Default {@link RoleService}, converting between {@link RoleBo} (API) and {@link RoleVo} (database).
 *
 * <p>{@link BeanUtils#copyProperties} copies same-named properties between the two. It skips
 * properties whose types don't match, such as {@code Set<PrivilegeBo>} vs {@code Set<PrivilegeVo>},
 * which is why privileges are converted by hand in each method.
 *
 * <p>Every method catches all exceptions, prints the stack trace and returns {@code false}/{@code null}.
 */
@Service
public class RoleServiceImpl implements RoleService {

	/** Role table access. */
	@Autowired
	RoleRepository roleRepository;

	/** Used to load the real privilege rows when creating a role. */
	@Autowired
	public PrivilegeRepository privilegeRepository;

	/** {@inheritDoc} */
	@Override
	public boolean isDuplicateRole(String roleName) {
		boolean isDuplicate = false;
		try {
			RoleVo role = roleRepository.findByRoleNameAndIsDeleted(roleName, false);
			if (role != null) {
				isDuplicate = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDuplicate;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Each privilege is loaded from the database by id, so the request only needs
	 * {@code privilegeId}. The new role is always active, whatever the request says. A role
	 * without privileges is never saved.
	 */
	@Override
	public boolean createRole(RoleBo roleBo) {
		boolean creationStatus = false;
		try {
			if (roleBo != null) {
				RoleVo roleVo = new RoleVo();
				roleVo.setRoleName(roleBo.getRoleName());
				roleVo.setActive(true);

				Set<PrivilegeVo> setVo = new HashSet<>();
				Set<PrivilegeBo> setBo = roleBo.getPrivileges();

				if (setBo != null && !setBo.isEmpty()) {
					for (PrivilegeBo privilegeBo : setBo) {

						PrivilegeVo privilegeVo = privilegeRepository
								.findById(privilegeBo.getPrivilegeId())
								.orElseThrow(() -> new RuntimeException(
										"Privilege not found: " + privilegeBo.getPrivilegeId()));
						setVo.add(privilegeVo);
					}
					roleVo.setPrivileges(setVo);
					roleVo = roleRepository.save(roleVo);
					if (roleVo != null && roleVo.getRoleId() > 0) {
						creationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return creationStatus;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Returns soft-deleted roles too. Privileges come back without their accesses.
	 */
	@Override
	public RoleBo getRoleByRoleId(int roleId) {
		RoleBo roleBo = null;
		try {
			RoleVo roleVo = roleRepository.findById(roleId).orElse(null);
			if (roleVo != null) {
				roleBo = new RoleBo();
				BeanUtils.copyProperties(roleVo, roleBo);
				Set<PrivilegeVo> setVo = roleVo.getPrivileges();
				Set<PrivilegeBo> setBo = new TreeSet<>(Comparator.comparing(PrivilegeBo::getPrivilegeName));
				if ((setVo != null) && (!setVo.isEmpty())) {
					for (PrivilegeVo privilegeVo : setVo) {
						PrivilegeBo privilegeBo = new PrivilegeBo();
						BeanUtils.copyProperties(privilegeVo, privilegeBo);
						setBo.add(privilegeBo);
					}
				}
				roleBo.setPrivileges(setBo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleBo;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Uses a "contains" query when {@code searchText} is given, otherwise all non-deleted roles.
	 * The database page is converted item by item into a new {@link PageImpl} with the same totals.
	 */
	@Override
	public Page<RoleBo> listRoles(int pageIndex, int pageSize, String sortOrder, String searchText) {
		Page<RoleBo> pageBo = null;
		try {
			Sort sort;
			if ((sortOrder != null) && (!sortOrder.isEmpty())) {
				if (sortOrder.equals("asc")) {
					sort = Sort.by("roleName").ascending();
				} else {
					sort = Sort.by("roleName").descending();
				}
			} else {
				sort = Sort.by("roleId").ascending();
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
			Page<RoleVo> pageVo = null;
			if ((searchText != null) && (!searchText.isEmpty())) {
				pageVo = roleRepository.findAllByRoleNameContainingIgnoreCaseAndIsDeleted(searchText, false, pageable);
			} else {
				pageVo = roleRepository.findAllByIsDeleted(false, pageable);
			}
			if (pageVo != null) {
				List<RoleVo> listVo = pageVo.getContent();
				List<RoleBo> listBo = new ArrayList<>();
				for (RoleVo roleVo : listVo) {
					if (roleVo != null) {
						RoleBo roleBo = new RoleBo();
						BeanUtils.copyProperties(roleVo, roleBo);
						listBo.add(roleBo);
					}
				}
				pageBo = new PageImpl<RoleBo>(listBo, pageable, pageVo.getTotalElements());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageBo;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Only the name and the privileges come from the request. The active and deleted flags are
	 * kept as stored: use {@code switch-activity} or {@code delete} to change them. (Copying the
	 * whole request with {@code BeanUtils} used to overwrite {@code active} with {@code false}
	 * whenever the client didn't send {@code "active": true}.) The privileges are rebuilt from the
	 * request (id and name each); the name is needed because the set is sorted by it.
	 */
	@Override
	public boolean updateRole(RoleBo roleBo) {
		boolean updationStatus = false;
		try {
			if (roleBo != null) {
				Set<PrivilegeBo> setBo = roleBo.getPrivileges();
				Set<PrivilegeVo> setVo =  new TreeSet<>(Comparator.comparing(PrivilegeVo::getPrivilegeName));
				RoleVo roleVo = roleRepository.findById(roleBo.getRoleId()).orElse(null);
				// Only the name: copying the whole Bo would reset isActive to false when the client omits "active".
				roleVo.setRoleName(roleBo.getRoleName());
				if ((setBo != null) && (!setBo.isEmpty())) {
					for (PrivilegeBo privilegeBo : setBo) {
						PrivilegeVo privilegeVo = new PrivilegeVo();
						BeanUtils.copyProperties(privilegeBo, privilegeVo);
						setVo.add(privilegeVo);
					}
					roleVo.setPrivileges(setVo);
					roleVo = roleRepository.save(roleVo);
					if ((roleVo != null) && roleVo.getRoleId() > 0) {
						updationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updationStatus;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>A missing role causes a {@code NullPointerException}, which is caught and reported as {@code false}.
	 */
	@Override
	public boolean deleteRole(int roleId) {
		boolean softDeletionStatus = false;
		try {
			if (roleId > 0) {
				RoleVo roleVo = roleRepository.findById(roleId).orElse(null);
				roleVo.setDeleted(true);
				roleVo = roleRepository.save(roleVo);
				if ((roleVo != null) && roleVo.getRoleId() > 0 && roleVo.isDeleted()) {
					softDeletionStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return softDeletionStatus;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>A missing role causes a {@code NullPointerException}, which is caught and reported as {@code false}.
	 */
	@Override
	public boolean switchActivity(int roleId) {
		boolean switchActivityStatus = false;
		try {
			if (roleId > 0) {
				RoleVo roleVo = roleRepository.findById(roleId).orElse(null);
				roleVo.setActive(!roleVo.isActive());
				roleVo = roleRepository.save(roleVo);
				if ((roleVo != null) && roleVo.getRoleId() > 0) {
					switchActivityStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return switchActivityStatus;
	}

}
