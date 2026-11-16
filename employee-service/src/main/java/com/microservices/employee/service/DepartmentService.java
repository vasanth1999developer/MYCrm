package com.microservices.employee.service;

import com.microservices.employee.model.DepartmentBo;

import java.util.List;

/**
 * Business logic for departments. Implemented by {@link DepartmentServiceImpl}.
 */
public interface DepartmentService {

    /**
     * Saves a new department.
     *
     * @param bo the department; only the name is used
     * @return the same object with its new id
     * @throws com.microservices.employee.common.DuplicateResourceException if the name exists
     *         (case-insensitive)
     */
    DepartmentBo createDepartment(DepartmentBo bo);

    /**
     * @return all departments that aren't soft-deleted
     */
    List<DepartmentBo> listDepartments();
}
