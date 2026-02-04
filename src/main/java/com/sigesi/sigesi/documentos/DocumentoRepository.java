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

  List<Documento> findByAssinanteOrderByDataDesc(String assinante);

  List<Documento> findByInteressadoOrderByDataDesc(String interessado);

  List<Documento> findByTipo(DocumentoTipo tipo);
}
