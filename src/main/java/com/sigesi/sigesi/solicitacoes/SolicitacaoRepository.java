package com.sigesi.sigesi.solicitacoes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório para Solicitacao.
 */
@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

  List<Solicitacao> findAllByOrderByIdAsc();

  List<Solicitacao> findByAutorIdOrderByDataDesc(Long autorId);

  List<Solicitacao> findByLocalIdOrderByDataDesc(Long localId);
}
