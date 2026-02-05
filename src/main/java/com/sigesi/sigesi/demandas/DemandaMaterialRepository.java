package com.sigesi.sigesi.demandas;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para DemandaMaterial.
 */
@Repository
public interface DemandaMaterialRepository
    extends JpaRepository<DemandaMaterial, Long> {

  Set<DemandaMaterial> findByDemandaId(Long demandaId);

  void deleteByDemandaId(Long demandaId);
}
