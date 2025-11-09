package com.sigesi.sigesi.pessoas.dtos;

import com.sigesi.sigesi.pessoas.SexoEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PessoaCreateDTO {

  @Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio ou composto apenas de espaços")
  @NotNull(message = "Nome não pode ser nulo")
  @Schema(description = "Nome da pessoa", example = "Pedro Silva")
  private String nome;

  @Pattern(regexp = ".*\\S.*", message = "O cpf não pode ser vazio ou composto apenas de espaços")
  @NotNull(message = "CPF não pode ser nulo")
  @Schema(description = "CPF da pessoa", example = "12345678900")
  private String cpf;

  @NotNull(message = "Sexo não pode ser nulo")
  @Schema(description = "Sexo da pessoa", example = "MASCULINO")
  private SexoEnum sexo;

  @NotNull(message = "Endereço não pode ser nulo")
  private Long enderecoId;
}
