package com.sigesi.sigesi.cemiterios.dtos;

import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CemiterioResponseDTO {

  private Long id;
  private String nome;
  private EnderecoResponseDTO endereco;

}
