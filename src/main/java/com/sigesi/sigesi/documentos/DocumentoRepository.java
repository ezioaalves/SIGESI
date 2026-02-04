package com.sigesi.sigesi.documentos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para Documento.
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

  List<Documento> findAllByOrderByIdAsc();

  List<Documento> findByAssinanteIdOrderByDataDesc(Long assinanteId);

  List<Documento> findByInteressadoIdOrderByDataDesc(Long interessadoId);

  List<Documento> findByTipo(DocumentoTipo tipo);
}
