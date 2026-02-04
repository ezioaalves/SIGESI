package com.sigesi.sigesi.documentos;

import com.sigesi.sigesi.arquivos.Arquivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Documento.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Documento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String numero;

  @Column(nullable = false)
  private LocalDate data;

  @NotBlank(message = "Subject e obrigatorio")
  @Column(nullable = false)
  private String subject;

  private String honorifico;

  @NotBlank(message = "Corpo e obrigatorio")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String body;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo")
  private DocumentoTipo tipo;

  private String portaria;

  @NotBlank(message = "Assinante e obrigatorio")
  @Column(nullable = false)
  private String assinante;

  @NotBlank(message = "Interessado e obrigatorio")
  @Column(nullable = false)
  private String interessado;

  private String destino;

  @jakarta.persistence.ManyToMany
// @formatter:off
  @jakarta.persistence.JoinTable(name = "documento_arquivos",
    joinColumns = @jakarta.persistence.JoinColumn(name = "documento_id"),
    inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "arquivo_id"))
// @formatter:on
  private java.util.List<Arquivo> anexos;

  @PrePersist
  protected void onCreate() {
    if (this.data == null) {
      this.data = LocalDate.now();
    }
  }
}
