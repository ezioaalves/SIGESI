package com.sigesi.sigesi.jazigos;

import com.sigesi.sigesi.cemiterios.Cemiterio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Entidade Jazigo.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jazigo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Cemitério é obrigatório")
  @ManyToOne
  @JoinColumn(name = "cemiterio_id", nullable = false)
  private Cemiterio cemiterio;

  private Double largura;
  private Double comprimento;

  @NotNull(message = "Quadra é obrigatória")
  @Column(nullable = false)
  private Integer quadra;

  @NotBlank(message = "Rua é obrigatória")
  @Column(nullable = false)
  private String rua;

  @NotBlank(message = "Lote é obrigatório")
  @Column(nullable = false)
  private String lote;
}
