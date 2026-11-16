package com.microservices.authorizationservice.entity;

import java.util.Set;

import javax.persistence.*;

import lombok.Data;

/**
 * JPA entity for the {@code role} table: a named bundle of privileges (e.g. {@code SALES_MANAGER}).
 *
 * <p>Users in authentication-service store role <b>names</b>, not ids, so the name is the link
 * between the two services. Only roles that are active and not soft-deleted grant privileges at
 * sign-in.
 */
@Entity
@Table(name="role")
@Data
public class RoleVo {

	/** Auto-increment primary key ({@code role_id}). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="role_id")
	private int roleId ;


	/** Role name ({@code role_name}); must match the names stored on users. */
	@Column(name="role_name")
	private String roleName ;

	/** Active flag ({@code is_active}); toggled by {@code switch-activity}. */
	@Column(name="is_active")
	private boolean isActive ;

	/** Soft-delete flag ({@code is_deleted}). */
	@Column(name="is_deleted")
	private boolean isDeleted ;

	/**
	 * Privileges granted by this role, stored in the join table {@code role_privilege}
	 * ({@code role_id}, {@code privilege_id}). Lazy-loaded and not cascaded.
	 */
	@ManyToMany
	@JoinTable( name = "role_privilege", joinColumns = @JoinColumn(name = "role_id"),inverseJoinColumns = @JoinColumn(name = "privilege_id"))
	private Set<PrivilegeVo> privileges ;

}
