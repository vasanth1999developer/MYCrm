package com.microservices.employee.repository;

import java.util.List;

import com.microservices.employee.entity.EmployeeVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;




@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeVo, Long> {


}
