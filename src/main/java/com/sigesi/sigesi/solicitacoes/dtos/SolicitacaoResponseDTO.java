package com.sigesi.sigesi.solicitacoes.dtos;

import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.usuarios.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para solicitação.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitacaoResponseDTO {

  private long id;

  @NotNull
  private LocalDate data;

  @NotBlank
  private String assunto;

  @NotBlank
  private String body;

  private String anexo;

  @NotNull
  private Usuario autor;

  @NotNull
  private Endereco local;
}
