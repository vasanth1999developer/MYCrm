package com.microservices.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.microservices.employee.entity.Employee;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@Modifying
    @Query("update Employee set isDelete=true where employeeId =?1")
	void deleteById(long empId);
	
	@Modifying
    @Query("from Employee where isDelete =false") 
	List<Employee>findAllEmployee();
}
