package com.clinch.crm.employee.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request and response body for a department: {@code {"departmentId": 1, "departmentName": "Sales"}}.
 */
@Data
public class DepartmentBo {

    /** The id; ignored on create, filled in the response. */
    private Long departmentId;

    /** Name, required, at most 50 characters. */
    @NotBlank(message = "Department name is required")
    @Size(max = 50, message = "Department name must not exceed 50 characters")
    private String departmentName;
}
