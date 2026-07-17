package com.sigesi.sigesi.documentos.dtos;

import com.sigesi.sigesi.config.ValidationLimits;
import com.sigesi.sigesi.documentos.DocumentoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criacao de documento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoCreateDTO {

  @Size(max = ValidationLimits.CODE, message = "Número deve ter no máximo 50 caracteres")
  private String numero;

  @NotBlank(message = "Assunto é obrigatório")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Assunto deve ter no máximo 150 caracteres")
  private String subject;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Honorífico deve ter no máximo 150 caracteres")
  private String honorifico;

  @NotBlank(message = "Corpo é obrigatório")
  @Size(max = ValidationLimits.DOCUMENT_BODY, message = "O corpo deve ter no máximo 20.000 caracteres")
  private String body;

  @NotNull(message = "Tipo é obrigatório")
  private DocumentoTipo tipo;

  @Size(max = ValidationLimits.CODE, message = "Portaria deve ter no máximo 50 caracteres")
  private String portaria;

  @NotBlank(message = "Assinante é obrigatório")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Assinante deve ter no máximo 150 caracteres")
  private String assinante;

  @NotBlank(message = "Interessado é obrigatório")
  @Size(max = ValidationLimits.SHORT_TEXT, message = "Interessado deve ter no máximo 150 caracteres")
  private String interessado;

  @Size(max = ValidationLimits.SHORT_TEXT, message = "Destino deve ter no máximo 150 caracteres")
  private String destino;

  @Size(max = 10, message = "É permitido anexar no máximo 10 arquivos")
  private java.util.List<Long> anexoIds;
}
