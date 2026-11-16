package com.microservices.employee.repository;

import com.microservices.employee.entity.EmployeeVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data repository for {@link EmployeeVo} (primary key = the shared user id).
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeVo, Long> {


    /**
     * Derived query: {@code WHERE is_deleted = ?}.
     *
     * @param delete the soft-delete flag to match ({@code false} for live employees)
     * @return all matching employees, unpaged
     */
    List<EmployeeVo> findAllByIsDeleted(Boolean delete);
}
