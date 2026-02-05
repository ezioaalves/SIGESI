package com.sigesi.sigesi.demandas.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criar item material com quantidade em uma demanda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandaMaterialCreateDTO {

  @NotNull(message = "Material é obrigatório")
  private Long materialId;

  @NotNull(message = "Quantidade é obrigatória")
  @Min(value = 1, message = "Quantidade deve ser maior que zero")
  private Integer quantidade;
}
