package com.sigesi.sigesi.pessoas.dtos;

import com.sigesi.sigesi.pessoas.SexoEnum;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PessoaUpdateDTO {
  @Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio ou composto apenas de espaços")
  private String nome;

  @Pattern(regexp = ".*\\S.*", message = "O cpf não pode ser vazio ou composto apenas de espaços")
  private String cpf;

  private SexoEnum sexo;

  private Long enderecoId;
}
