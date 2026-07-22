package com.sigesi.sigesi.jazigos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.cemiterios.Cemiterio;
import com.sigesi.sigesi.enderecos.Endereco;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitários para a entidade Jazigo.
 */
@DisplayName("Jazigo Entity Tests")
class JazigoEntityTest {

  private Validator validator;
  private Cemiterio cemiterioValido;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    Endereco endereco = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    cemiterioValido = Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Central")
        .endereco(endereco)
        .build();
  }

  @Test
  @DisplayName("Deve lançar violação quando cemitério é nulo")
  void testCemiterioNaoPodeSerNulo() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(null)
        .quadra(1)
        .rua("A")
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("cemiterio")
            && v.getMessage().equals("Cemitério é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando quadra é nula")
  void testQuadraNaoPodeSerNula() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(null)
        .rua("A")
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("quadra")
            && v.getMessage().equals("Quadra é obrigatória")));
  }

  @Test
  @DisplayName("Deve lançar violação quando rua é nula")
  void testRuaNaoPodeSerNula() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(1)
        .rua(null)
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("rua")
            && v.getMessage().equals("Rua é obrigatória")));
  }

  @Test
  @DisplayName("Deve lançar violação quando rua é vazia")
  void testRuaNaoPodeSerVazia() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(1)
        .rua("")
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("rua")
            && v.getMessage().equals("Rua é obrigatória")));
  }

  @Test
  @DisplayName("Deve lançar violação quando lote é nulo")
  void testLoteNaoPodeSerNulo() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(1)
        .rua("A")
        .lote(null)
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("lote")
            && v.getMessage().equals("Lote é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando lote é vazio")
  void testLoteNaoPodeSerVazio() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(1)
        .rua("A")
        .lote("")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("lote")
            && v.getMessage().equals("Lote é obrigatório")));
  }

  @Test
  @DisplayName("Deve criar jazigo válido sem violações")
  void testCriacaoJazigoValido() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .quadra(1)
        .rua("A")
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertTrue(violations.isEmpty());
    assertEquals(cemiterioValido, jazigo.getCemiterio());
    assertEquals(1, jazigo.getQuadra());
    assertEquals("A", jazigo.getRua());
    assertEquals("10", jazigo.getLote());
  }

  @Test
  @DisplayName("Deve criar jazigo válido com dimensões")
  void testCriacaoJazigoComDimensoes() {
    Jazigo jazigo = Jazigo.builder()
        .cemiterio(cemiterioValido)
        .largura(2.5)
        .comprimento(3.0)
        .quadra(1)
        .rua("A")
        .lote("10")
        .build();

    Set<ConstraintViolation<Jazigo>> violations = validator.validate(jazigo);

    assertTrue(violations.isEmpty());
    assertEquals(2.5, jazigo.getLargura());
    assertEquals(3.0, jazigo.getComprimento());
  }

  @Test
  @DisplayName("Deve criar jazigo usando padrão Builder")
  void testBuilderPattern() {
    Jazigo jazigo = Jazigo.builder()
        .id(1L)
        .cemiterio(cemiterioValido)
        .largura(2.0)
        .comprimento(2.5)
        .quadra(2)
        .rua("B")
        .lote("20")
        .build();

    assertNotNull(jazigo);
    assertEquals(1L, jazigo.getId());
    assertEquals(cemiterioValido, jazigo.getCemiterio());
    assertEquals(2.0, jazigo.getLargura());
    assertEquals(2.5, jazigo.getComprimento());
    assertEquals(2, jazigo.getQuadra());
    assertEquals("B", jazigo.getRua());
    assertEquals("20", jazigo.getLote());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Jazigo jazigo = new Jazigo();
    jazigo.setId(10L);
    jazigo.setCemiterio(cemiterioValido);
    jazigo.setLargura(3.0);
    jazigo.setComprimento(3.5);
    jazigo.setQuadra(5);
    jazigo.setRua("C");
    jazigo.setLote("50");

    assertEquals(10L, jazigo.getId());
    assertEquals(cemiterioValido, jazigo.getCemiterio());
    assertEquals(3.0, jazigo.getLargura());
    assertEquals(3.5, jazigo.getComprimento());
    assertEquals(5, jazigo.getQuadra());
    assertEquals("C", jazigo.getRua());
    assertEquals("50", jazigo.getLote());
  }
}
