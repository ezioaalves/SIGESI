package com.sigesi.sigesi.solicitacoes.dtos;

import com.sigesi.sigesi.solicitacoes.SolicitacaoStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de solicitação.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoUpdateDTO {

  @NotNull(message = "Status não pode ser vazio")
  @Schema(description = "Status da solicitação", example = "ABERTO")
  private SolicitacaoStatus status;

}
