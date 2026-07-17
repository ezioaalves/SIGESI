package com.sigesi.sigesi.documentos.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import com.sigesi.sigesi.documentos.DocumentoTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualizacao de documento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoUpdateDTO {

  @Size(max = ValidationLimits.CODE, message = "Número deve ter no máximo 50 caracteres")
  private String numero;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Assunto deve ter no máximo 150 caracteres")
  private String subject;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Honorífico deve ter no máximo 150 caracteres")
  private String honorifico;

  @Size(max = ValidationLimits.DOCUMENT_BODY, message = "O corpo deve ter no máximo 20.000 caracteres")
  private String body;

  @Schema(description = "Tipo do documento", example = "OFICIO")
  private DocumentoTipo tipo;

  @Size(max = ValidationLimits.CODE, message = "Portaria deve ter no máximo 50 caracteres")
  private String portaria;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Assinante deve ter no máximo 150 caracteres")
  private String assinante;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Interessado deve ter no máximo 150 caracteres")
  private String interessado;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Destino deve ter no máximo 150 caracteres")
  private String destino;

  @Size(max = 10, message = "É permitido anexar no máximo 10 arquivos")
  private java.util.List<Long> anexoIds;
}
