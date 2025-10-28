package com.sigesi.sigesi.enderecos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  private String logradouro;
  private String numero;
  private String bairro;
  private String referencia;
}
