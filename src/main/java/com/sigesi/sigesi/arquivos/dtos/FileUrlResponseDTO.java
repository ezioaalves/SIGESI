package com.sigesi.sigesi.arquivos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for file download URLs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUrlResponseDTO {

  private Long fileId;
  private String url;
  private Long expiresInSeconds;
  private String nomeOriginal;
}
