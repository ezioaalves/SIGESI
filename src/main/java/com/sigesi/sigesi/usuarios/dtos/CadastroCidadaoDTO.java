package com.sigesi.sigesi.usuarios.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.pessoas.SexoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dados necessários para concluir o primeiro cadastro do cidadão autenticado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CadastroCidadaoDTO {

  @NotBlank(message = "Nome é obrigatório")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  @NotBlank(message = "CPF é obrigatório")
  @Size(max = ValidationLimits.CPF, message = "CPF deve ter no máximo 14 caracteres")
  private String cpf;

  @NotNull(message = "Sexo é obrigatório")
  private SexoEnum sexo;

  @Valid
  @NotNull(message = "Endereço é obrigatório")
  private EnderecoCreateDTO endereco;
}
