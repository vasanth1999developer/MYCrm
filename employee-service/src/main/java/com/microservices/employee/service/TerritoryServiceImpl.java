package com.microservices.employee.service;

import com.microservices.employee.common.DuplicateResourceException;
import com.microservices.employee.entity.TerritoryVo;
import com.microservices.employee.model.TerritoryBo;
import com.microservices.employee.repository.TerritoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default {@link TerritoryService}. Same logic as {@link DepartmentServiceImpl}.
 */
@Service
@RequiredArgsConstructor
public class TerritoryServiceImpl implements TerritoryService {

    /** Territory table access (constructor-injected by Lombok). */
    private final TerritoryRepository territoryRepository;

    /**
     * {@inheritDoc}
     *
     * <p>The duplicate check also counts soft-deleted territories; the database's unique
     * constraint is the final guard against simultaneous requests.
     */
    @Override
    public TerritoryBo createTerritory(TerritoryBo bo) {
        if (territoryRepository.existsByTerritoryNameIgnoreCase(bo.getTerritoryName())) {
            throw new DuplicateResourceException(
                    "Territory already exists: " + bo.getTerritoryName());
        }

        TerritoryVo vo = new TerritoryVo();
        vo.setTerritoryName(bo.getTerritoryName());

        TerritoryVo saved = territoryRepository.save(vo);

        bo.setTerritoryId(saved.getTerritoryId());
        return bo;
    }

    /** {@inheritDoc} */
    @Override
    public List<TerritoryBo> listTerritories() {
        return territoryRepository.findByIsDeletedFalse().stream()
                .map(vo -> {
                    TerritoryBo bo = new TerritoryBo();
                    bo.setTerritoryId(vo.getTerritoryId());
                    bo.setTerritoryName(vo.getTerritoryName());
                    return bo;
                })
                .collect(Collectors.toList());
    }
}
