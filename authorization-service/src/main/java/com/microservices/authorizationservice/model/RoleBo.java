package com.microservices.authorizationservice.model;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * API representation of a role:
 * {@code {"roleId": 1, "roleName": "SALES_MANAGER", "active": true, "privileges": [...]}}.
 *
 * <p>Lombok turns the boolean field {@code isActive} into {@code isActive()}/{@code setActive()},
 * so the JSON key is <b>{@code active}</b>; a request key {@code isActive} is ignored.
 */
@Data
public class RoleBo {

	/** The id; 0 when creating. */
	private int roleId ;

	/** The name; required. */
	@NotNull(message = "Role name must not be null")
    @NotEmpty(message = "Role name must not be empty")
	private String roleName ;

	/** Whether the role grants privileges (JSON key {@code active}). Output only: create always makes an active role, update keeps the stored flag. */
	private boolean isActive ;

	/** Privileges of this role; {@code null} in list responses. */
	private Set<PrivilegeBo> privileges;

}
