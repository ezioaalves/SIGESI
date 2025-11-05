package com.sigesi.sigesi.enderecos.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoCreateDTO {

  @NotBlank(message = "Logradouro é obrigatório")
  @Column(nullable = false)
  private String logradouro;

  @NotBlank(message = "Número é obrigatório")
  @Column(nullable = false)
  private String numero;

  @NotBlank(message = "Bairro é obrigatório")
  @Column(nullable = false)
  private String bairro;

  private String referencia;

}
