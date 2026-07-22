package com.sigesi.sigesi.jazigos.dtos;

import com.sigesi.sigesi.cemiterios.Cemiterio;

import lombok.Data;

@Data
public class JazigoResponseDTO {

  private Long id;
  private Cemiterio cemiterio;
  private Double largura;
  private Double comprimento;
  private Integer quadra;
  private String rua;
  private String lote;

}
