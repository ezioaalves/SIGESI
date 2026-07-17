package com.sigesi.sigesi.enderecos.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoCreateDTO {

  @NotBlank(message = "Logradouro é obrigatório")
  @Size(max = ValidationLimits.ADDRESS, message = "Logradouro deve ter no máximo 255 caracteres")
  @Column(nullable = false)
  private String logradouro;

  @NotBlank(message = "Número é obrigatório")
  @Size(max = ValidationLimits.CODE, message = "Número deve ter no máximo 50 caracteres")
  @Column(nullable = false)
  private String numero;

  @NotBlank(message = "Bairro é obrigatório")
  @Size(max = ValidationLimits.ADDRESS, message = "Bairro deve ter no máximo 255 caracteres")
  @Column(nullable = false)
  private String bairro;

  @Size(max = ValidationLimits.ADDRESS, message = "Referência deve ter no máximo 255 caracteres")
  private String referencia;

}
