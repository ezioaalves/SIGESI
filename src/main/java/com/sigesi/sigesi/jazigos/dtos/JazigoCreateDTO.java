package com.sigesi.sigesi.jazigos.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JazigoCreateDTO {
  @NotNull(message = "Cemitério é obrigatório")
  private Long cemiterio;

  private Double largura;
  private Double comprimento;

  @NotNull(message = "Quadra é obrigatória")
  private Integer quadra;

  @NotNull(message = "Rua é obrigatória")
  @NotBlank(message = "Rua não pode ser vazio")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Rua deve ter no máximo 150 caracteres")
  private String rua;

  @NotNull(message = "Lote é obrigatório")
  @NotBlank(message = "Lote não pode ser vazio")
  @Size(max = ValidationLimits.CODE, message = "Lote deve ter no máximo 50 caracteres")
  private String lote;
}
