package com.sigesi.sigesi.documentos.dtos;

import com.sigesi.sigesi.documentos.DocumentoTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualizacao de documento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoUpdateDTO {

  private String numero;

  private String subject;

  private String honorifico;

  private String body;

  @Schema(description = "Tipo do documento", example = "OFICIO")
  private DocumentoTipo tipo;

  private String portaria;

  private String assinante;

  private String interessado;

  private String destino;

  private java.util.List<Long> anexoIds;
}
