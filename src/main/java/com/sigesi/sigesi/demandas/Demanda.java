package com.sigesi.sigesi.demandas;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.solicitacoes.Solicitacao;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma demanda de trabalho.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Demanda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Solicitação é obrigatória")
  @ManyToOne
  @JoinColumn(name = "solicitacao_id", nullable = false)
  private Solicitacao solicitacao;

  @ManyToOne
  @JoinColumn(name = "responsavel_id")
  private Usuario responsavel;

  @Column(nullable = false)
  private LocalDate prazo;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DemandaStatus status;

  @Builder.Default
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "demanda_material",
      joinColumns = @JoinColumn(name = "demanda_id"),
      inverseJoinColumns = @JoinColumn(name = "material_id")
  )
  private Set<Material> materiais = new HashSet<>();

  /**
   * Define status padrao antes de persistir.
   */
  @PrePersist
  protected void onCreate() {
    if (this.status == null) {
      this.status = DemandaStatus.PENDENTE;
    }
  }

  /**
   * Adiciona material a demanda.
   */
  public void addMaterial(Material material) {
    materiais.add(material);
  }

  /**
   * Remove material da demanda.
   */
  public void removeMaterial(Material material) {
    materiais.remove(material);
  }
}
