package com.microservices.employee.repository;

import com.microservices.employee.entity.DepartmentVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<DepartmentVo, Long> {
    boolean existsByDepartmentNameIgnoreCase(String departmentName);
    List<DepartmentVo> findByIsDeletedFalse();   // list only non-deleted
}
