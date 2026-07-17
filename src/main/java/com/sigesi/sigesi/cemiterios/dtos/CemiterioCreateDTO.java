package com.sigesi.sigesi.cemiterios.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CemiterioCreateDTO {

  @NotBlank(message = "Nome não pode ser vazio")
  @NotNull(message = "Nome é obrigatório")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  @NotNull(message = "Endereço é obrigatório")
  private Long endereco;
}
