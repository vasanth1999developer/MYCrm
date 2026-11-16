package com.microservices.employee.repository;

import com.microservices.employee.entity.EmployeeVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeVo, Long> {


    List<EmployeeVo> findAllByIsDeleted(Boolean delete);
}
