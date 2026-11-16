package com.clinch.crm.employee.controller;

import com.clinch.crm.employee.common.ApiResponse;
import com.clinch.crm.employee.model.DepartmentBo;
import com.clinch.crm.employee.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST endpoints for departments, under {@code /employee/department} (JWT required at the gateway).
 *
 * <p>Departments are a lookup list (e.g. for a dropdown). Employees store the department as a
 * free-text name, so this table isn't enforced on employees. There's no update or delete yet.
 *
 * <p>This service has no exception handler, so errors use Spring Boot's default error JSON
 * ({@code timestamp}, {@code status}, {@code error}, {@code path}), not {@link ApiResponse}.
 */
@RestController
@RequestMapping("/employee/department")
@RequiredArgsConstructor
public class DepartmentController {

    /** Department business logic (constructor-injected by Lombok). */
    private final DepartmentService departmentService;

    /**
     * {@code POST /employee/department/create}: body {@code {"departmentName": "Sales"}}.
     *
     * @param bo the department; name required, at most 50 characters
     * @return 201 with the saved department ({@code departmentId}, {@code departmentName});
     *         400 if the name is blank or too long; <b>500</b> if the name already exists
     *         (case-insensitive), since {@code DuplicateResourceException} has no handler
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DepartmentBo>> create(
            @Valid @RequestBody DepartmentBo bo) {
        DepartmentBo saved = departmentService.createDepartment(bo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created", saved));
    }

    /**
     * {@code GET /employee/department/list}: all departments that aren't soft-deleted.
     *
     * @return 200 with the list (possibly empty)
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DepartmentBo>>> list() {
        List<DepartmentBo> departments = departmentService.listDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched", departments));
    }
}
