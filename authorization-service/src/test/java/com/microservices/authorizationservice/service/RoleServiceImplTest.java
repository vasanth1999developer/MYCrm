package com.microservices.authorizationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.microservices.authorizationservice.entity.RoleVo;
import com.microservices.authorizationservice.model.PrivilegeBo;
import com.microservices.authorizationservice.model.RoleBo;
import com.microservices.authorizationservice.repository.RoleRepository;

/**
 * Unit tests for {@link RoleServiceImpl#updateRole}, with a mocked {@link RoleRepository}
 * (no database).
 */
class RoleServiceImplTest {

    private RoleRepository roleRepository;
    private RoleServiceImpl service;
    /** The stored role: id 7, active, not deleted. */
    private RoleVo stored;

    /** Wires the service to a mock repository that returns {@link #stored} for id 7. */
    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        service = new RoleServiceImpl();
        service.roleRepository = roleRepository;

        stored = new RoleVo();
        stored.setRoleId(7);
        stored.setRoleName("SALES_REP");
        stored.setActive(true);
        stored.setDeleted(false);
        stored.setPrivileges(new HashSet<>());

        when(roleRepository.findById(7)).thenReturn(Optional.of(stored));
        when(roleRepository.save(any(RoleVo.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    /** An update request as most clients send it: no {@code active} field, so it parses as {@code false}. */
    private static RoleBo renameRequest() {
        PrivilegeBo privilege = new PrivilegeBo();
        privilege.setPrivilegeId(3);
        privilege.setPrivilegeName("LEAD_READ");

        RoleBo bo = new RoleBo();
        bo.setRoleId(7);
        bo.setRoleName("SALES_EXECUTIVE");
        bo.setPrivileges(Set.of(privilege));
        return bo;
    }

    /** Updating a role must not deactivate it when the request omits {@code active}. */
    @Test
    void updateKeepsActiveFlag() {
        assertTrue(service.updateRole(renameRequest()));

        ArgumentCaptor<RoleVo> saved = ArgumentCaptor.forClass(RoleVo.class);
        verify(roleRepository).save(saved.capture());
        assertTrue(saved.getValue().isActive(), "update must not switch the role off");
        assertFalse(saved.getValue().isDeleted());
    }

    /** The name and privileges in the request are still applied. */
    @Test
    void updateAppliesNameAndPrivileges() {
        assertTrue(service.updateRole(renameRequest()));

        ArgumentCaptor<RoleVo> saved = ArgumentCaptor.forClass(RoleVo.class);
        verify(roleRepository).save(saved.capture());
        assertEquals("SALES_EXECUTIVE", saved.getValue().getRoleName());
        assertEquals(1, saved.getValue().getPrivileges().size());
        assertEquals(3, saved.getValue().getPrivileges().iterator().next().getPrivilegeId());
    }

    /** An inactive role stays inactive after an update (only switch-activity changes it). */
    @Test
    void updateKeepsInactiveRoleInactive() {
        stored.setActive(false);
        RoleBo request = renameRequest();
        request.setActive(true);

        assertTrue(service.updateRole(request));

        ArgumentCaptor<RoleVo> saved = ArgumentCaptor.forClass(RoleVo.class);
        verify(roleRepository).save(saved.capture());
        assertFalse(saved.getValue().isActive());
    }

    /** A role that doesn't exist reports {@code false} and saves nothing. */
    @Test
    void updateOfMissingRoleFails() {
        RoleBo request = renameRequest();
        request.setRoleId(99);
        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertFalse(service.updateRole(request));
    }
}
