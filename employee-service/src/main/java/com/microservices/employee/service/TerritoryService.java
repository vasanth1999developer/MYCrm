package com.microservices.employee.service;

import com.microservices.employee.model.TerritoryBo;

import java.util.List;

public interface TerritoryService {
    TerritoryBo createTerritory(TerritoryBo bo);
    List<TerritoryBo> listTerritories();
}
