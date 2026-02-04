package com.sigesi.sigesi.documentos.dtos;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.documentos.DocumentoTipo;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para documento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentoResponseDTO {

  private long id;

  private String numero;

  private LocalDate data;

  private String subject;

  private String honorifico;

  private String body;

  private DocumentoTipo tipo;

  private String portaria;

  private String assinante;

  private String interessado;

  private String destino;

  private java.util.List<ArquivoResponseDTO> anexos;
}
