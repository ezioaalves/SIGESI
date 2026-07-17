package com.sigesi.sigesi.materiais.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  @NotNull(message = "Preço é obrigatório")
  private Double preco;
}
