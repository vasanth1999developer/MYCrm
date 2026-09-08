package com.microservices.employee.controller;

import com.microservices.employee.common.ApiResponse;
import com.microservices.employee.model.TerritoryBo;
import com.microservices.employee.service.TerritoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employee/territory")
@RequiredArgsConstructor
public class TerritoryController {

    private final TerritoryService territoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TerritoryBo>> create(
            @Valid @RequestBody TerritoryBo bo) {
        TerritoryBo saved = territoryService.createTerritory(bo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Territory created", saved));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TerritoryBo>>> list() {
        List<TerritoryBo> territories = territoryService.listTerritories();
        return ResponseEntity.ok(ApiResponse.success("Territories fetched", territories));
    }
}