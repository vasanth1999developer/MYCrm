package com.microservices.employee.controller;

import com.microservices.employee.common.ApiResponse;
import com.microservices.employee.model.DepartmentBo;
import com.microservices.employee.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employee/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DepartmentBo>> create(
            @Valid @RequestBody DepartmentBo bo) {
        DepartmentBo saved = departmentService.createDepartment(bo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created", saved));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DepartmentBo>>> list() {
        List<DepartmentBo> departments = departmentService.listDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched", departments));
    }
}