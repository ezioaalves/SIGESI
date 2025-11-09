package com.sigesi.sigesi.enderecos.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoUpdateDTO {
  @NotBlank(message = "Logradouro não pode ser vazio")
  @Schema(description = "Nome da rua", example = "Rua das flores")
  private String logradouro;

  @NotBlank(message = "Numero não pode ser vazio")
  @Schema(description = "Numero da endereço", example = "00")
  private String numero;

  @NotBlank(message = "bairro não pode ser vazio")
  @Schema(description = "Nome do bairro", example = "Centro")
  private String bairro;

  @NotBlank(message = "Referencia não pode ser vazio")
  @Schema(example = "Próximo aos correios")
  private String referencia;
}
