package com.sigesi.sigesi.demandas.dtos;

import java.time.LocalDate;
import java.util.List;

import com.sigesi.sigesi.demandas.DemandaStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualizacao de Demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandaUpdateDTO {

  @Schema(description = "ID do responsável (agente)", example = "1")
  private Long responsavelId;

  @Schema(description = "Prazo da demanda", example = "2025-12-31")
  private LocalDate prazo;

  @Schema(description = "Status da demanda", example = "EM_ANDAMENTO")
  private DemandaStatus status;

  @Schema(description = "Materiais com quantidade")
  private List<DemandaMaterialCreateDTO> materiais;
}
