package com.sigesi.sigesi.demandas.dtos;

import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para DemandaMaterial com material e quantidade.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandaMaterialResponseDTO {

  private Long id;
  private MaterialResponseDTO material;
  private Integer quantidade;
}
