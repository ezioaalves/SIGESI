package com.sigesi.sigesi.gavetas.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GavetaCreateDTO {

  @NotNull(message = "jazigo é obrigatorio")
  private Long jazigo;

  @NotNull(message = "numero é obrigatorio")
  private Integer numero;

  @NotNull(message = "Ocupante é obrigatorio")
  private Long ocupante;
}
