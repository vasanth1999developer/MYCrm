package com.microservices.authendicationservice.models;

/**
 * Legacy fixed list of role names, used only by the unused {@link Role} entity.
 *
 * <p>Current roles are free-form strings managed by authorization-service and stored on
 * {@link User#getRoles()}, so this enum doesn't limit them.
 */
public enum ERole {
	/** Legacy employee role. */
	ROLE_EMPLOYEE,
	/** Legacy company role. */
	ROLE_COMPANY,
	/** Legacy administrator role. */
    ROLE_ADMIN
}
