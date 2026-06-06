package com.microservices.authorizationservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microservices.authorizationservice.entity.PrivilegeVo;
import com.microservices.authorizationservice.entity.RoleVo;
import com.microservices.authorizationservice.model.PrivilegeBo;
import com.microservices.authorizationservice.model.RoleBo;
import com.microservices.authorizationservice.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleRepository roleRepository;

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

	@Override
	public boolean createRole(RoleBo roleBo) {
		boolean creationStatus = false;
		try {
			if (roleBo != null) {
				RoleVo roleVo = new RoleVo();
				roleVo.setRoleName(roleBo.getRoleName());
				roleVo.setActive(true);
				Set<PrivilegeVo> setVo = new TreeSet<>(Comparator.comparing(PrivilegeVo::getPrivilegeName));
				Set<PrivilegeBo> setBo = roleBo.getPrivileges();
				if ((setBo != null) && (!setBo.isEmpty())) {
					for (PrivilegeBo privilegeBo : setBo) {
						PrivilegeVo privilegeVo = new PrivilegeVo();
						BeanUtils.copyProperties(privilegeBo, privilegeVo);
						setVo.add(privilegeVo);
					}
					roleVo.setPrivileges(setVo);
					roleVo = roleRepository.save(roleVo);
					if ((roleVo != null) && roleVo.getRoleId() > 0) {
						creationStatus = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return creationStatus;
	}

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

	@Override
	public boolean updateRole(RoleBo roleBo) {
		boolean updationStatus = false;
		try {
			if (roleBo != null) {
				Set<PrivilegeBo> setBo = roleBo.getPrivileges();
				Set<PrivilegeVo> setVo =  new TreeSet<>(Comparator.comparing(PrivilegeVo::getPrivilegeName));
				RoleVo roleVo = roleRepository.findById(roleBo.getRoleId()).orElse(null);
				BeanUtils.copyProperties(roleBo, roleVo);
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
