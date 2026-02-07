package com.sigesi.sigesi.documentos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitarios para DocumentoPdfService.
 */
@DisplayName("DocumentoPdfService Tests")
class DocumentoPdfServiceTest {

  private DocumentoPdfService documentoPdfService;

  @BeforeEach
  void setUp() {
    documentoPdfService = new DocumentoPdfService();
  }

  private Documento criarDocumentoBase(DocumentoTipo tipo) {
    return Documento.builder()
        .numero("001/2025")
        .data(LocalDate.of(2025, 6, 15))
        .subject("Assunto de teste")
        .honorifico("Senhor Secretario")
        .body("Corpo do documento de teste para validacao.")
        .tipo(tipo)
        .assinante("Joao da Silva")
        .interessado("Maria dos Santos")
        .portaria("Portaria 123/2025")
        .build();
  }

  @Test
  @DisplayName("Deve gerar PDF tipo OFICIO com sucesso")
  void testGerarPdfOficio() {
    Documento doc = criarDocumentoBase(DocumentoTipo.OFICIO);

    byte[] result = documentoPdfService.gerarPdfDocumento(doc);

    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  @Test
  @DisplayName("Deve gerar PDF tipo MEMORANDO com sucesso")
  void testGerarPdfMemorando() {
    Documento doc = criarDocumentoBase(DocumentoTipo.MEMORANDO);

    byte[] result = documentoPdfService.gerarPdfDocumento(doc);

    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  @Test
  @DisplayName("Deve gerar PDF com campos opcionais nulos")
  void testGerarPdfComCamposNulos() {
    Documento doc = Documento.builder()
        .numero("002/2025")
        .data(LocalDate.of(2025, 6, 15))
        .subject("Assunto")
        .honorifico("Senhor")
        .body("Corpo do documento.")
        .tipo(DocumentoTipo.OFICIO)
        .assinante("Assinante")
        .interessado("Interessado")
        .build();

    byte[] result = documentoPdfService.gerarPdfDocumento(doc);

    assertNotNull(result);
    assertTrue(result.length > 0);
  }
}
