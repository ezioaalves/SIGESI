package com.sigesi.sigesi.solicitacoes.dtos;

import com.sigesi.sigesi.solicitacoes.SolicitacaoAssunto;
import com.sigesi.sigesi.solicitacoes.SolicitacaoStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de solicitação.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoCreateDTO {

  @NotNull(message = "Assunto é obrigatório")
  private SolicitacaoAssunto assunto;

  @NotNull(message = "Status é obrigatório")
  private SolicitacaoStatus status;

  @NotBlank(message = "Corpo é obrigatório")
  private String body;

  private Long anexoId;

  @NotNull(message = "Autor é obrigatório")
  private Long autorId;

  @NotNull(message = "Local é obrigatório")
  private Long localId;
}
