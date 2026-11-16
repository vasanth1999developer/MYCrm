package com.clinch.crm.authorization.entity;

import javax.persistence.*;

import lombok.Data;

/**
 * JPA entity for the {@code access} table: the smallest permission unit (e.g. "view leads").
 *
 * <p>Linked to privileges through the {@code privilege_access} join table (owned by
 * {@link PrivilegeVo}). Lombok {@code @Data} generates getters/setters; for the boolean
 * {@code isDeleted} they are {@code isDeleted()}/{@code setDeleted()}, so its JSON key is
 * {@code deleted}.
 */
@Entity
@Table(name="access")
@Data
public class AccessVo {

	/** Auto-increment primary key ({@code access_id}). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="access_id")
	private int accessId ;


	/** Name of the access entry ({@code access_name}); unique among non-deleted entries (checked in code). */
	@Column(name="access_name")
	private String accessName ;

	/** Soft-delete flag ({@code is_deleted}); deleted rows stay in the table. */
	@Column(name="is_deleted")
	private boolean isDeleted ;
}
