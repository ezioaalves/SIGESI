package com.sigesi.sigesi.materiais;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um material.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Material {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Nome é obrigatório")
  @Column(nullable = false)
  private String nome;

  @NotNull(message = "Preço é obrigatório")
  @Column(nullable = false)
  private Double preco;
}
