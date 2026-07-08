package com.sigesi.sigesi.solicitacoes.dtos;

import com.sigesi.sigesi.solicitacoes.SolicitacaoAssunto;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;

import jakarta.validation.Valid;
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

  @NotBlank(message = "Corpo é obrigatório")
  private String body;

  private java.util.List<Long> anexoIds;

  private Long autorId;

  private Long solicitanteId;

  @Valid
  private PessoaCreateDTO solicitante;

  @NotNull(message = "Local é obrigatório")
  private Long localId;
}
