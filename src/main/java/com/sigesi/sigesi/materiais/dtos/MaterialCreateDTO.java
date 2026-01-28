package com.sigesi.sigesi.materiais.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialCreateDTO {

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @NotNull(message = "Preço é obrigatório")
  private Double preco;
}
