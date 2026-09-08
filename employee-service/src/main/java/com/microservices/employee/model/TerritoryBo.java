package com.microservices.employee.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TerritoryBo {

    private Long territoryId;

    @NotBlank(message = "Territory name is required")
    @Size(max = 50, message = "Territory name must not exceed 50 characters")
    private String territoryName;
}