package com.sigesi.sigesi.pessoas.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sigesi.sigesi.enderecos.Endereco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PessoaResponseDTO {
  private Long id;
  private String nome;
  private String cpf;
  private String sexo;
  private Endereco endereco;
}
