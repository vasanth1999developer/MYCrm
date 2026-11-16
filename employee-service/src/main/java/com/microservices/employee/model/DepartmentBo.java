package com.microservices.employee.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DepartmentBo {

    private Long departmentId;

    @NotBlank(message = "Department name is required")
    @Size(max = 50, message = "Department name must not exceed 50 characters")
    private String departmentName;
}
