package com.sigesi.sigesi.comentarios.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
  private String texto;
}
