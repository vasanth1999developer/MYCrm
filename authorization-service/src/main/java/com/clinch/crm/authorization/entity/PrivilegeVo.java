package com.clinch.crm.authorization.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * JPA entity for the {@code privilege} table: a named permission (e.g. {@code LEAD_READ}) that
 * groups access entries. Its name is what goes into the JWT.
 *
 * <p>Note the soft-delete column here is {@code is_delete} (field {@code isDelete}), while roles
 * and accesses use {@code is_deleted}.
 */
@Entity
@Data
@Table(name="privilege")
public class PrivilegeVo {


	/** Auto-increment primary key ({@code privilege_Id}). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="privilege_Id")
	private int privilegeId;

	/** Privilege name ({@code privilege_Name}), e.g. {@code LEAD_READ}. */
	@Column(name="privilege_Name")
	private String privilegeName;

	/** Soft-delete flag ({@code is_delete}). Not checked at sign-in. */
	@Column(name = "is_delete")
	private boolean isDelete;

	/**
	 * Access entries in this privilege, stored in the join table {@code privilege_access}
	 * ({@code privilege_id}, {@code access_id}). Lazy-loaded (the {@code @ManyToMany} default)
	 * and not cascaded: saving a privilege links existing accesses but never creates them.
	 */
	@ManyToMany
	@JoinTable( name = "privilege_access", joinColumns = @JoinColumn(name = "privilege_id"),inverseJoinColumns = @JoinColumn(name = "access_id"))
	private Set<AccessVo> accesses ;


}
