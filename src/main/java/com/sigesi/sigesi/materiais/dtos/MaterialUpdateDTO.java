package com.sigesi.sigesi.materiais.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualizacao de Material.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUpdateDTO {

  @Schema(description = "Nome do material", example = "Cimento")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  @Schema(description = "Preço do material", example = "50.00")
  private Double preco;
}
