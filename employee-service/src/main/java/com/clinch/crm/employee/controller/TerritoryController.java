package com.clinch.crm.employee.controller;

import com.clinch.crm.employee.common.ApiResponse;
import com.clinch.crm.employee.model.TerritoryBo;
import com.clinch.crm.employee.service.TerritoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST endpoints for sales territories, under {@code /employee/territory} (JWT required at the
 * gateway).
 *
 * <p>Same design as {@link DepartmentController}: a lookup list, not linked to employees, no
 * update/delete, and Spring Boot's default error JSON on failures.
 */
@RestController
@RequestMapping("/employee/territory")
@RequiredArgsConstructor
public class TerritoryController {

    /** Territory business logic (constructor-injected by Lombok). */
    private final TerritoryService territoryService;

    /**
     * {@code POST /employee/territory/create}: body {@code {"territoryName": "South"}}.
     *
     * @param bo the territory; name required, at most 50 characters
     * @return 201 with the saved territory ({@code territoryId}, {@code territoryName});
     *         400 if the name is blank or too long; <b>500</b> if the name already exists
     *         (case-insensitive)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TerritoryBo>> create(
            @Valid @RequestBody TerritoryBo bo) {
        TerritoryBo saved = territoryService.createTerritory(bo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Territory created", saved));
    }

    /**
     * {@code GET /employee/territory/list}: all territories that aren't soft-deleted.
     *
     * @return 200 with the list (possibly empty)
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TerritoryBo>>> list() {
        List<TerritoryBo> territories = territoryService.listTerritories();
        return ResponseEntity.ok(ApiResponse.success("Territories fetched", territories));
    }
}
