package com.sigesi.sigesi.pessoas;

import com.sigesi.sigesi.enderecos.Endereco;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Pessoa.
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pessoa {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotBlank
  private String nome;

  @NotBlank
  @NotNull
  private String cpf;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SexoEnum sexo;

  @ManyToOne
  @JoinColumn(name = "endereco_id")
  private Endereco endereco;
}
