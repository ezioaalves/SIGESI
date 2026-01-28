package com.sigesi.sigesi.materiais.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialResponseDTO {

  private Long id;
  private String nome;
  private Double preco;
}
