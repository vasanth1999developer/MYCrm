package com.microservices.employee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.employee.entity.Employee;
import com.microservices.employee.model.EmployeeBo;
import com.microservices.employee.repository.EmployeeRepository;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Override
	public EmployeeBo createEmployee(EmployeeBo employee) {
		Employee employeeVo = new Employee();
		try {
			BeanUtils.copyProperties(employee, employeeVo);
			employeeVo = employeeRepository.save(employeeVo);
			BeanUtils.copyProperties(employeeVo, employee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public List<EmployeeBo> getAllEmployee() {
		List<Employee> EmployeeList = new ArrayList<>();
		List<EmployeeBo> EmployeeBoList = new ArrayList<>();
		long sNo=0;
		try {
			EmployeeList = employeeRepository.findAllEmployee();
			for( Employee emp : EmployeeList) {
				EmployeeBo bo = new EmployeeBo();
				bo.setEmployeeId(emp.getEmployeeId());
				bo.setFirstName(emp.getFirstName());
				bo.setLastName(emp.getLastName());
				bo.setEmailAddress(emp.getEmailAddress());
				bo.setPassword(emp.getPassword());
				//bo.setConfirmPassword(emp.getConfirmPassword());
				bo.setMobileNo(emp.getMobileNo());
				bo.setSectionNo(++sNo);
				EmployeeBoList.add(bo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EmployeeBoList;
	}

	@Override
	public EmployeeBo updateEmployee(EmployeeBo employeeBo) {
		Employee employee = new Employee();
		try {
			BeanUtils.copyProperties(employeeBo, employee);
			employee = employeeRepository.save(employee);
			employeeBo.setEmployeeId(employee.getEmployeeId());
			BeanUtils.copyProperties(employee, employeeBo);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return employeeBo;
	}

	@Override
	public EmployeeBo getEmployeeById(long employeeId) {
		EmployeeBo employeeBO=new EmployeeBo();
		try {
			Employee employeeVO=employeeRepository.findById(employeeId).orElse(new Employee());
		if(null!=employeeVO) {
			employeeBO.setEmployeeId(employeeVO.getEmployeeId());
			employeeBO.setFirstName(employeeVO.getFirstName());
			employeeBO.setLastName(employeeVO.getLastName());
			employeeBO.setMobileNo(employeeVO.getMobileNo());
			employeeBO.setEmailAddress(employeeVO.getEmailAddress());
			employeeBO.setPassword(employeeVO.getPassword());
			//employeeBO.setConfirmPassword(employeeVO.getConfirmPassword());
			employeeBO.setAddress(employeeVO.getAddress());
			employeeBO.setState(employeeVO.getState());
			employeeBO.setCountry(employeeVO.getCountry());
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
			return employeeBO;
		}

	@Override
	public boolean deleteemployee(long employeeId) {
		employeeRepository.deleteById(employeeId);
		return true;
	}

}
