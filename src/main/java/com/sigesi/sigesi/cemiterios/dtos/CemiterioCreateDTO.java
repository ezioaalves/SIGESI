package com.sigesi.sigesi.cemiterios.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CemiterioCreateDTO {

  @NotBlank(message = "Nome não pode ser vazio")
  @NotNull(message = "Nome é obrigatirio")
  private String nome;

  @NotNull(message = "Cemiterio é obrigatirio")
  private Long endereco;
}
