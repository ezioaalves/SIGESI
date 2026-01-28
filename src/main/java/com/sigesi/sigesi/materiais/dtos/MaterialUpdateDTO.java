package com.sigesi.sigesi.materiais.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualizacao de Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUpdateDTO {

  @Schema(description = "Nome do material", example = "Cimento")
  private String nome;

  @Schema(description = "Preço do material", example = "50.00")
  private Double preco;
}
