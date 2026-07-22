package com.sigesi.sigesi.gavetas.dtos;

import com.sigesi.sigesi.jazigos.Jazigo;
import com.sigesi.sigesi.pessoas.Pessoa;

import lombok.Data;

@Data
public class GavetaResponseDTO {

  private Long id;
  private Jazigo jazigo;
  private Integer numero;
  private Pessoa ocupante;

}
