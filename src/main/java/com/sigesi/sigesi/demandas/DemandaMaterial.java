package com.sigesi.sigesi.demandas;

import org.hibernate.envers.Audited;

import com.sigesi.sigesi.materiais.Material;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade intermediaria entre Demanda e Material com quantidade.
 */
@Entity
@Table(name = "demanda_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
public class DemandaMaterial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "demanda_id", nullable = false)
  private Demanda demanda;

  @ManyToOne
  @JoinColumn(name = "material_id", nullable = false)
  private Material material;

  @NotNull(message = "Quantidade é obrigatória")
  @Column(nullable = false)
  private Integer quantidade;
}
