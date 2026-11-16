package com.microservices.employee.service;

import com.microservices.employee.common.DuplicateResourceException;
import com.microservices.employee.entity.DepartmentVo;
import com.microservices.employee.model.DepartmentBo;
import com.microservices.employee.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default {@link DepartmentService}. Maps fields by hand (no {@code BeanUtils}).
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    /** Department table access (constructor-injected by Lombok). */
    private final DepartmentRepository departmentRepository;

    /**
     * {@inheritDoc}
     *
     * <p>The duplicate check also counts soft-deleted departments. Two simultaneous requests with
     * the same name can both pass it; the database's unique constraint then rejects the second.
     */
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

    /** {@inheritDoc} */
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
