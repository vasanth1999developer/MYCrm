package com.microservices.employee.repository;

import com.microservices.employee.entity.TerritoryVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerritoryRepository extends JpaRepository<TerritoryVo, Long> {
    boolean existsByTerritoryNameIgnoreCase(String territoryName);
    List<TerritoryVo> findByIsDeletedFalse();
}
