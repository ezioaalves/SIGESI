package com.sigesi.sigesi.gavetas;

import com.sigesi.sigesi.jazigos.Jazigo;
import com.sigesi.sigesi.pessoas.Pessoa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Gaveta.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Gaveta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Jazigo é obrigatório")
  @ManyToOne
  @JoinColumn(name = "jazigo_id", nullable = false)
  private Jazigo jazigo;

  private Integer numero;

  @ManyToOne
  @JoinColumn(name = "ocupante_id")
  private Pessoa ocupante;
}
