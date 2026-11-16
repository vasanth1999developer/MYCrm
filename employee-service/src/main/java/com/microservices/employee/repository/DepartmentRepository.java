package com.microservices.employee.repository;

import com.microservices.employee.entity.DepartmentVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link DepartmentVo}; both methods are derived queries built from
 * the method name.
 */
public interface DepartmentRepository extends JpaRepository<DepartmentVo, Long> {
    /**
     * @param departmentName the name
     * @return {@code true} if any department (deleted or not) has this name, ignoring case
     */
    boolean existsByDepartmentNameIgnoreCase(String departmentName);

    /** @return departments with {@code isDeleted = false} */
    List<DepartmentVo> findByIsDeletedFalse();   // list only non-deleted
}
