package com.microservices.authorizationservice.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="access")
@Data
public class AccessVo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="access_id")
	private int accessId ;


	@Column(name="access_name")
	private String accessName ;

	@Column(name="is_deleted")
	private boolean isDeleted ;
}
