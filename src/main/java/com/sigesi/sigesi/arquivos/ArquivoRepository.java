package com.sigesi.sigesi.arquivos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Arquivo.
 */
@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {

  List<Arquivo> findByAtivoTrueOrderByUploadedAtDesc();

  List<Arquivo> findByCategoriaOrderByUploadedAtDesc(String categoria);
}
