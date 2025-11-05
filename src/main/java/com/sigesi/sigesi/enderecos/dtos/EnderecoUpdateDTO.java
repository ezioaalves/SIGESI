package com.sigesi.sigesi.enderecos.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoUpdateDTO {
  @NotBlank(message = "Logradouro não pode ser vazio")
  private String logradouro;

  @NotBlank(message = "Numero não pode ser vazio")
  private String numero;

  @NotBlank(message = "bairro não pode ser vazio")
  private String bairro;

  @NotBlank(message = "Referencia não pode ser vazio")
  private String referencia;
}
