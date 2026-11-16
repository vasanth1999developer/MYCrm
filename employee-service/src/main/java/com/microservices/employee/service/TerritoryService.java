package com.microservices.employee.service;

import com.microservices.employee.model.TerritoryBo;

import java.util.List;

/**
 * Business logic for territories. Implemented by {@link TerritoryServiceImpl}.
 */
public interface TerritoryService {
    /**
     * Saves a new territory.
     *
     * @param bo the territory; only the name is used
     * @return the same object with its new id
     * @throws com.microservices.employee.common.DuplicateResourceException if the name exists
     *         (case-insensitive)
     */
    TerritoryBo createTerritory(TerritoryBo bo);

    /**
     * @return all territories that aren't soft-deleted
     */
    List<TerritoryBo> listTerritories();
}
