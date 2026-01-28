package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandaCreateDTO {

  @NotNull(message = "Solicitação é obrigatória")
  private Long solicitacaoId;

  private Long responsavelId;

  @NotNull(message = "Prazo é obrigatório")
  private LocalDate prazo;

  private Set<Long> materiaisIds;
}
