package com.sigesi.sigesi.jazigos.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JazigoCreateDTO {
  @NotNull(message = "Cemiterio é obrigatorio")
  private Long cemiterio;

  private Double largura;
  private Double comprimento;

  @NotNull(message = "Quadra é obrigatorio")
  private Integer quadra;

  @NotNull(message = "Rua é obrigatorio")
  @NotBlank(message = "Rua não pode ser vazio")
  private String rua;

  @NotNull(message = "Lote é obrigatorio")
  @NotBlank(message = "Lote não pode ser vazio")
  private String lote;
}
