package com.sigesi.sigesi.enderecos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoUpdateDTO {
  private String logradouro;
  private String numero;
  private String bairro;
  private String referencia;
}
