package com.sigesi.sigesi.comentarios.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de Comentario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioCreateDTO {

  @NotNull(message = "Demanda é obrigatória")
  private Long demandaId;

  @NotNull(message = "Autor é obrigatório")
  private Long autorId;

  @NotBlank(message = "Texto é obrigatório")
  @Size(max = ValidationLimits.LONG_TEXT, message = "O comentário deve ter no máximo 5.000 caracteres")
  private String texto;
}
