package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.enderecos.Endereco;
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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Solicitacao.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Solicitacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate data;

  @NotBlank(message = "Assunto é obrigatório")
  @Column(nullable = false)
  private String assunto;

  @NotBlank(message = "Corpo é obrigatório")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String body;

  @ManyToOne
  @JoinColumn(name = "anexo_id")
  private Arquivo anexo;

  @NotNull(message = "Autor é obrigatório")
  @ManyToOne
  @JoinColumn(name = "autor_id", nullable = false)
  private Usuario autor;

  @NotNull(message = "Local é obrigatório")
  @ManyToOne
  @JoinColumn(name = "local_id", nullable = false)
  private Endereco local;

  @PrePersist
  protected void onCreate() {
    if (this.data == null) {
      this.data = LocalDate.now();
    }
  }
}
