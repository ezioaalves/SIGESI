package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Comentario.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

  List<Comentario> findAllByOrderByIdAsc();

  List<Comentario> findByDemandaIdOrderByCriadoEmAsc(Long demandaId);

  List<Comentario> findByAutorIdOrderByCriadoEmDesc(Long autorId);
}
