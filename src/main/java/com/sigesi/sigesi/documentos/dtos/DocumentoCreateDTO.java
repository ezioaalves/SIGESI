package com.sigesi.sigesi.documentos.dtos;

import com.sigesi.sigesi.documentos.DocumentoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  private String numero;

  @NotBlank(message = "Subject e obrigatorio")
  private String subject;

  private String honorifico;

  @NotBlank(message = "Corpo e obrigatorio")
  private String body;

  @NotNull(message = "Tipo e obrigatorio")
  private DocumentoTipo tipo;

  private String portaria;

  @NotNull(message = "Assinante e obrigatorio")
  private Long assinanteId;

  @NotNull(message = "Interessado e obrigatorio")
  private Long interessadoId;

  private String destino;

  private java.util.List<Long> anexoIds;
}
