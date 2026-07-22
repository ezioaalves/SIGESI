package com.sigesi.sigesi.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.documentos.DocumentoTipo;
import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.solicitacoes.SolicitacaoAssunto;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Limites de texto e anexos")
class TextValidationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Deve rejeitar endereço acima dos limites")
  void testEnderecoAcimaDoLimite() {
    EnderecoCreateDTO dto = new EnderecoCreateDTO(
        "A".repeat(ValidationLimits.ADDRESS + 1),
        "1".repeat(ValidationLimits.CODE + 1),
        "Centro",
        null);

    assertFalse(validator.validate(dto).isEmpty());
  }

  @Test
  @DisplayName("Deve aceitar textos longos no limite")
  void testTextosLongosNoLimite() {
    ComentarioCreateDTO comentario = new ComentarioCreateDTO(
        1L, 1L, "A".repeat(ValidationLimits.LONG_TEXT));
    DocumentoCreateDTO documento = new DocumentoCreateDTO(
        "1", "Assunto", null, "A".repeat(ValidationLimits.DOCUMENT_BODY),
        DocumentoTipo.MEMORANDO, null, "Assinante", "Interessado", null, null);

    assertTrue(validator.validate(comentario).isEmpty());
    assertTrue(validator.validate(documento).isEmpty());
  }

  @Test
  @DisplayName("Deve rejeitar mais de dez anexos")
  void testSolicitacaoComMaisDeDezAnexos() {
    SolicitacaoCreateDTO dto = new SolicitacaoCreateDTO(
        SolicitacaoAssunto.OUTROS,
        "Descrição",
        Collections.nCopies(11, 1L),
        1L,
        null,
        null,
        1L);

    assertFalse(validator.validate(dto).isEmpty());
  }
}
