package com.sigesi.sigesi.comentarios;

import java.time.LocalDateTime;

import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um comentario em uma demanda.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comentario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Demanda é obrigatória")
  @ManyToOne
  @JoinColumn(name = "demanda_id", nullable = false)
  private Demanda demanda;

  @NotNull(message = "Autor é obrigatório")
  @ManyToOne
  @JoinColumn(name = "autor_id", nullable = false)
  private Usuario autor;

  @NotBlank(message = "Texto é obrigatório")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String texto;

  @Column(nullable = false)
  private LocalDateTime criadoEm;

  /**
   * Define data de criacao antes de persistir.
   */
  @PrePersist
  protected void onCreate() {
    if (this.criadoEm == null) {
      this.criadoEm = LocalDateTime.now();
    }
  }
}
