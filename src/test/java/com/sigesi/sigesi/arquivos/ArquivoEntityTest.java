package com.sigesi.sigesi.arquivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitarios para a entidade Arquivo.
 */
@DisplayName("Arquivo Entity Tests")
class ArquivoEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve criar entidade valida sem violacoes")
  void testCriacaoValida() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("uploads/foto.jpg")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando nomeOriginal e nulo")
  void testNomeOriginalNulo() {
    Arquivo arquivo = Arquivo.builder()
        .storageKey("key")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nomeOriginal")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando nomeOriginal e vazio")
  void testNomeOriginalVazio() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("")
        .storageKey("key")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nomeOriginal")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando nomeOriginal e apenas espacos")
  void testNomeOriginalApenasEspacos() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("   ")
        .storageKey("key")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nomeOriginal")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando storageKey e nulo")
  void testStorageKeyNulo() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("storageKey")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando storageKey e vazio")
  void testStorageKeyVazio() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("storageKey")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando contentType e nulo")
  void testContentTypeNulo() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("key")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("contentType")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando contentType e vazio")
  void testContentTypeVazio() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("key")
        .contentType("")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("contentType")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando tamanho e nulo")
  void testTamanhoNulo() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("key")
        .contentType("image/jpeg")
        .uploadedAt(LocalDateTime.now())
        .build();

    Set<ConstraintViolation<Arquivo>> violations = validator.validate(arquivo);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("tamanho")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Arquivo arquivo = Arquivo.builder()
        .id(1L)
        .nomeOriginal("foto.jpg")
        .storageKey("uploads/foto.jpg")
        .contentType("image/jpeg")
        .tamanho(2048L)
        .categoria("documentos")
        .uploadedAt(LocalDateTime.now())
        .ativo(true)
        .build();

    assertNotNull(arquivo);
    assertEquals(1L, arquivo.getId());
    assertEquals("foto.jpg", arquivo.getNomeOriginal());
    assertEquals("uploads/foto.jpg", arquivo.getStorageKey());
    assertEquals("documentos", arquivo.getCategoria());
  }

  @Test
  @DisplayName("Deve definir uploadedAt via PrePersist")
  void testOnCreateSetsUploadedAt() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("key")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .build();

    arquivo.onCreate();

    assertNotNull(arquivo.getUploadedAt());
  }

  @Test
  @DisplayName("Deve ter ativo como true por padrao")
  void testDefaultAtivoTrue() {
    Arquivo arquivo = Arquivo.builder()
        .nomeOriginal("foto.jpg")
        .storageKey("key")
        .contentType("image/jpeg")
        .tamanho(1024L)
        .uploadedAt(LocalDateTime.now())
        .build();

    assertTrue(arquivo.getAtivo());
  }
}
