package com.clinch.crm.authentication.models;

import javax.persistence.*;

/**
 * Legacy JPA entity for the {@code roles} table. Unused by the current code.
 *
 * <p>Roles used to be rows linked to users; they're now plain strings in
 * {@link User#getRoles()} (table {@code user_roles}). Hibernate still creates this table because
 * the entity exists ({@code ddl-auto=update}).
 */
@Entity
@Table(name = "roles")
public class Role {
	/** Auto-increment primary key. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Role name, stored as text (e.g. {@code ROLE_ADMIN}). */
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;

	/** No-arg constructor required by JPA. */
	public Role() {

	}

	/** @param name the role name */
	public Role(ERole name) {
		this.name = name;
	}

	/** @return the primary key */
	public Integer getId() {
		return id;
	}

	/** @param id the primary key */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @return the role name */
	public ERole getName() {
		return name;
	}

	/** @param name the role name */
	public void setName(ERole name) {
		this.name = name;
	}
}
