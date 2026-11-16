package com.microservices.employee.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request and response body for a territory: {@code {"territoryId": 1, "territoryName": "South"}}.
 */
@Data
public class TerritoryBo {

    /** The id; ignored on create, filled in the response. */
    private Long territoryId;

    /** Name, required, at most 50 characters. */
    @NotBlank(message = "Territory name is required")
    @Size(max = 50, message = "Territory name must not exceed 50 characters")
    private String territoryName;
}
