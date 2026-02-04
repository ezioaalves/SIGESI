package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.Set;

import com.sigesi.sigesi.demandas.DemandaStatus;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.usuarios.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandaResponseDTO {

  private Long id;
  private SolicitacaoResponseDTO solicitacao;
  private Usuario responsavel;
  private LocalDate prazo;
  private DemandaStatus status;
  private Set<MaterialResponseDTO> materiais;
}
