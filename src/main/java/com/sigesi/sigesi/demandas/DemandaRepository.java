package com.sigesi.sigesi.demandas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Demanda.
 */
@Repository
public interface DemandaRepository extends JpaRepository<Demanda, Long> {

  List<Demanda> findAllByOrderByIdAsc();

  List<Demanda> findBySolicitacaoIdOrderByPrazoAsc(Long solicitacaoId);

  List<Demanda> findByResponsavelIdOrderByPrazoAsc(Long responsavelId);

  List<Demanda> findByStatusOrderByPrazoAsc(DemandaStatus status);
}
