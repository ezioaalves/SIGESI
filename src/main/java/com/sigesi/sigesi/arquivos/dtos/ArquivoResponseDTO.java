package com.sigesi.sigesi.arquivos.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Arquivo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoResponseDTO {

  private Long id;
  private String nomeOriginal;
  private String storageKey;
  private String contentType;
  private Long tamanho;
  private String categoria;
  private LocalDateTime uploadedAt;
  private Boolean ativo;
}
