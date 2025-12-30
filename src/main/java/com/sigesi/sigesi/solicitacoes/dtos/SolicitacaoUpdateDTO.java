package com.sigesi.sigesi.solicitacoes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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

  @NotBlank(message = "Assunto não pode ser vazio")
  @Schema(description = "Assunto da solicitação", example = "Solicitação de manutenção")
  private String assunto;

  @NotBlank(message = "Corpo não pode ser vazio")
  @Schema(description = "Descrição detalhada", example = "Detalhes da solicitação...")
  private String body;

  @Schema(description = "Caminho do arquivo anexo", example = "/docs/anexo.pdf")
  private String anexo;

  @NotNull(message = "Autor não pode ser nulo")
  @Schema(description = "ID do usuário autor", example = "1")
  private Long autorId;

  @NotNull(message = "Local não pode ser nulo")
  @Schema(description = "ID do endereço", example = "1")
  private Long localId;
}
