package com.sigesi.sigesi.demandas;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidade que representa uma demanda de trabalho.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Demanda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
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
  @OneToMany(mappedBy = "demanda",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<DemandaMaterial> materiais = new HashSet<>();

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
   * Adiciona DemandaMaterial a demanda.
   */
  public void addDemandaMaterial(DemandaMaterial item) {
    materiais.add(item);
    item.setDemanda(this);
  }

  /**
   * Remove DemandaMaterial da demanda.
   */
  public void removeDemandaMaterial(DemandaMaterial item) {
    materiais.remove(item);
    item.setDemanda(null);
  }
}
