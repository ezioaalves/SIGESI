package com.sigesi.sigesi.documentos;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitarios para a entidade Documento.
 */
@DisplayName("Documento Entity Tests")
class DocumentoEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve lancar violacao quando subject e nulo")
  void testSubjectNaoPodeSerNulo() {
    Documento documento = Documento.builder()
        .subject(null)
        .body("Corpo do documento")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("subject")
            && v.getMessage().equals("Assunto é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando subject e vazio")
  void testSubjectNaoPodeSerVazio() {
    Documento documento = Documento.builder()
        .subject("")
        .body("Corpo do documento")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("subject")
            && v.getMessage().equals("Assunto é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando subject e apenas espacos")
  void testSubjectNaoPodeSerApenasEspacos() {
    Documento documento = Documento.builder()
        .subject("   ")
        .body("Corpo do documento")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("subject")
            && v.getMessage().equals("Assunto é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando body e nulo")
  void testBodyNaoPodeSerNulo() {
    Documento documento = Documento.builder()
        .subject("Assunto do documento")
        .body(null)
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("body")
            && v.getMessage().equals("Corpo é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando body e vazio")
  void testBodyNaoPodeSerVazio() {
    Documento documento = Documento.builder()
        .subject("Assunto do documento")
        .body("")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("body")
            && v.getMessage().equals("Corpo é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando assinante e nulo")
  void testAssinanteNaoPodeSerNulo() {
    Documento documento = Documento.builder()
        .subject("Assunto do documento")
        .body("Corpo do documento")
        .assinante(null)
        .interessado("Maria Santos")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("assinante")
            && v.getMessage().equals("Assinante é obrigatório")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando interessado e nulo")
  void testInteressadoNaoPodeSerNulo() {
    Documento documento = Documento.builder()
        .subject("Assunto do documento")
        .body("Corpo do documento")
        .assinante("João Silva")
        .interessado(null)
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("interessado")
            && v.getMessage().equals("Interessado é obrigatório")));
  }

  @Test
  @DisplayName("Deve criar documento valido sem violacoes")
  void testCriarDocumentoValido() {
    Documento documento = Documento.builder()
        .numero("001/2026")
        .subject("Assunto do documento")
        .honorifico("Excelentissimo Senhor")
        .body("Corpo do documento")
        .tipo(DocumentoTipo.OFICIO)
        .portaria("Portaria 123/2026")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .destino("Secretaria Municipal")
        .build();

    Set<ConstraintViolation<Documento>> violations = validator.validate(documento);

    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve testar Builder pattern")
  void testBuilderPattern() {
    Documento documento = Documento.builder()
        .subject("Test Subject")
        .body("Test Body")
        .assinante("João Silva")
        .interessado("Maria Santos")
        .build();

    assertNotNull(documento);
    assertEquals("Test Subject", documento.getSubject());
    assertEquals("Test Body", documento.getBody());
  }

  @Test
  @DisplayName("Deve testar Lombok getters e setters")
  void testLombokGettersSetters() {
    Documento documento = new Documento();
    documento.setSubject("Test Subject");
    documento.setBody("Test Body");
    documento.setTipo(DocumentoTipo.MEMORANDO);

    assertEquals("Test Subject", documento.getSubject());
    assertEquals("Test Body", documento.getBody());
    assertEquals(DocumentoTipo.MEMORANDO, documento.getTipo());
  }
}
