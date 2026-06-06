package com.microservices.account.entity;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import lombok.Data;


@MappedSuperclass
@Data
public class BaseVO implements Serializable {
private static final long serialVersionUID = -5074230273317960920L;

    
    private long createdBy;
    private Date created = new Date();
    private Date modified = new Date();
    private long modifiedBy;

}
