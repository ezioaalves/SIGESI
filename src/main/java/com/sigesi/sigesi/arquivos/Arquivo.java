package com.sigesi.sigesi.arquivos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import org.hibernate.envers.Audited;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "arquivos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Arquivo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String nomeOriginal;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String storageKey;

  @NotBlank
  @Column(nullable = false)
  private String contentType;

  @NotNull
  @Column(nullable = false)
  private Long tamanho;

  private String categoria;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime uploadedAt;

  @Builder.Default
  private Boolean ativo = true;

  @PrePersist
  protected void onCreate() {
    uploadedAt = LocalDateTime.now();
  }
}
