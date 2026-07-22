package com.sigesi.sigesi.gavetas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GavetaRepository extends JpaRepository<Gaveta, Long> {
  List<Gaveta> findAllByOrderByIdAsc();

  List<Gaveta> findByJazigoId(Long jazigoId);

  List<Gaveta> findByOcupanteId(Long ocupanteId);

  List<Gaveta> findByJazigoIdAndOcupanteId(Long jazigoId, Long ocupanteId);

}
