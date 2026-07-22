package com.sigesi.sigesi.gavetas.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GavetaUpdateDTO {

  @Schema(description = "ID do jazigo", example = "1")
  private Long jazigo;

  @Schema(description = "Numero da gabeta", example = "1")
  private Integer numero;

  @Schema(description = "Id do ocupante", example = "1")
  private Long ocupante;

}
