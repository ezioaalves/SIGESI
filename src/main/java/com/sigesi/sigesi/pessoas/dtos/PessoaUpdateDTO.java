package com.sigesi.sigesi.pessoas.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import com.sigesi.sigesi.pessoas.SexoEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PessoaUpdateDTO {
  @Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio ou composto apenas de espaços")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Nome deve ter no máximo 150 caracteres")
  @Schema(description = "Nome da pessoa", example = "Fulano da Silva")
  private String nome;

  @Pattern(regexp = ".*\\S.*", message = "O cpf não pode ser vazio ou composto apenas de espaços")
  @Size(max = ValidationLimits.CPF, message = "CPF deve ter no máximo 14 caracteres")
  @Schema(description = "CPF da Pessoa", example = "12345678900")
  private String cpf;

  @Schema(description = "Sexo da pessoa", example = "MASCULINO")
  private SexoEnum sexo;

  @Schema(description = "ID do endereço", example = "1")
  private Long enderecoId;
}
