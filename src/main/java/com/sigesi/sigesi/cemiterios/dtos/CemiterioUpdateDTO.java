package com.sigesi.sigesi.cemiterios.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CemiterioUpdateDTO {

  @Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio ou composto apenas de espaços")
  @Schema(description = "Nome do cemiterio", example = "Cemiterio são lucas")
  private String nome;

  @Schema(description = "ID do endereço", example = "1")
  private Long endereco;
}
