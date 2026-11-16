package com.microservices.employee.service;

import com.microservices.employee.model.DepartmentBo;

import java.util.List;

public interface DepartmentService {

    DepartmentBo createDepartment(DepartmentBo bo);
    List<DepartmentBo> listDepartments();
}
