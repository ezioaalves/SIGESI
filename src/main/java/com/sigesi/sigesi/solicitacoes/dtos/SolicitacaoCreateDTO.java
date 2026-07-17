package com.sigesi.sigesi.solicitacoes.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import com.sigesi.sigesi.solicitacoes.SolicitacaoAssunto;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
  @Size(max = ValidationLimits.LONG_TEXT, message = "A descrição deve ter no máximo 5.000 caracteres")
  private String body;

  @Size(max = 10, message = "É permitido anexar no máximo 10 arquivos")
  private java.util.List<Long> anexoIds;

  private Long autorId;

  private Long solicitanteId;

  @Valid
  private PessoaCreateDTO solicitante;

  @NotNull(message = "Local é obrigatório")
  private Long localId;
}
