package com.microservices.employee.service;

import com.microservices.employee.entity.DepartmentVo;
import com.microservices.employee.model.DepartmentBo;
import com.microservices.employee.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentBo createDepartment(DepartmentBo bo) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(bo.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists: " + bo.getDepartmentName());
        }

        DepartmentVo vo = new DepartmentVo();
        vo.setDepartmentName(bo.getDepartmentName());

        DepartmentVo saved = departmentRepository.save(vo);

        bo.setDepartmentId(saved.getDepartmentId());
        return bo;
    }

    @Override
    public List<DepartmentBo> listDepartments() {
        return departmentRepository.findByIsDeletedFalse().stream()
                .map(vo -> {
                    DepartmentBo bo = new DepartmentBo();
                    bo.setDepartmentId(vo.getDepartmentId());
                    bo.setDepartmentName(vo.getDepartmentName());
                    return bo;
                })
                .collect(Collectors.toList());
    }
}
