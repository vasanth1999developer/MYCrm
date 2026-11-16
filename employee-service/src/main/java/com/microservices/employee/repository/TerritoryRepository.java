package com.microservices.employee.repository;

import com.microservices.employee.entity.TerritoryVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link TerritoryVo}; both methods are derived queries built from the
 * method name.
 */
public interface TerritoryRepository extends JpaRepository<TerritoryVo, Long> {
    /**
     * @param territoryName the name
     * @return {@code true} if any territory (deleted or not) has this name, ignoring case
     */
    boolean existsByTerritoryNameIgnoreCase(String territoryName);

    /** @return territories with {@code isDeleted = false} */
    List<TerritoryVo> findByIsDeletedFalse();
}
