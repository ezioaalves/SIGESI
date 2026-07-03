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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DemandaMaterial {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "demanda_id", nullable = false)
  @ToString.Exclude
  private Demanda demanda;

  @ManyToOne
  @JoinColumn(name = "material_id", nullable = false)
  @EqualsAndHashCode.Include
  private Material material;

  @NotNull(message = "Quantidade é obrigatória")
  @Column(nullable = false)
  private Integer quantidade;
}
