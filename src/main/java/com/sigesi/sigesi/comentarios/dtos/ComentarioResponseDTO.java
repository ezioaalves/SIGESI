package com.sigesi.sigesi.comentarios.dtos;

import java.time.LocalDateTime;

import com.sigesi.sigesi.usuarios.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Comentario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComentarioResponseDTO {

  private Long id;
  private Long demandaId;
  private Usuario autor;
  private String texto;
  private LocalDateTime criadoEm;
}
