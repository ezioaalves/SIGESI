package com.sigesi.sigesi.enderecos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Endereco.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Endereco {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Logradouro é obrigatório")
  @Column(nullable = false)
  private String logradouro;

  @NotBlank(message = "Número é obrigatório")
  @Column(nullable = false)
  private String numero;

  @NotBlank(message = "Bairro é obrigatório")
  @Column(nullable = false)
  private String bairro;

  private String referencia;
}
