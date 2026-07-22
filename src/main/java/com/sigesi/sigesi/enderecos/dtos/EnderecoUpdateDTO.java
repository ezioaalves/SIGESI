package com.sigesi.sigesi.enderecos.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoUpdateDTO {
  @NotBlank(message = "Logradouro não pode ser vazio")
  @Size(max = ValidationLimits.ADDRESS, message = "Logradouro deve ter no máximo 255 caracteres")
  @Schema(description = "Nome da rua", example = "Rua das flores")
  private String logradouro;

  @NotBlank(message = "Número não pode ser vazio")
  @Size(max = ValidationLimits.CODE, message = "Número deve ter no máximo 50 caracteres")
  @Schema(description = "Numero da endereço", example = "00")
  private String numero;

  @NotBlank(message = "Bairro não pode ser vazio")
  @Size(max = ValidationLimits.ADDRESS, message = "Bairro deve ter no máximo 255 caracteres")
  @Schema(description = "Nome do bairro", example = "Centro")
  private String bairro;

  @Size(max = ValidationLimits.ADDRESS, message = "Referência deve ter no máximo 255 caracteres")
  @Schema(example = "Próximo aos correios")
  private String referencia;
}
