package com.microservices.authorizationservice.entity;

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
@Entity
@Data
@Table(name="privilege")
public class PrivilegeVo {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="privilege_Id")
	private int privilegeId;
	
	@Column(name="privilege_Name")
	private String privilegeName;
	
	private boolean isDelete;
	
	@ManyToMany
	@JoinTable( name = "privilege_access", joinColumns = @JoinColumn(name = "privilege_id"),inverseJoinColumns = @JoinColumn(name = "access_id"))
	private Set<AccessVo> accesses ;
	

}
