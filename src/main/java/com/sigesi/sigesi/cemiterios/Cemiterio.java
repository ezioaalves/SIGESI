package com.sigesi.sigesi.cemiterios;

import com.sigesi.sigesi.enderecos.Endereco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Cemiterio.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cemiterio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Nome é obrigatório")
  @Column(nullable = false)
  private String nome;

  @NotNull(message = "Endereço é obrigatório")
  @OneToOne
  @JoinColumn(name = "endereco_id", nullable = false)
  private Endereco endereco;
}
