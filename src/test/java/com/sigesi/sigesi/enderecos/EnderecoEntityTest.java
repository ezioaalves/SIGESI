package com.sigesi.sigesi.enderecos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitários para a entidade Endereco.
 */
@DisplayName("Endereco Entity Tests")
class EnderecoEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve lançar violação quando logradouro é nulo")
  void testLogradouroNaoPodeSerNulo() {
    Endereco endereco = Endereco.builder()
        .logradouro(null)
        .numero("123")
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")
            && v.getMessage().equals("Logradouro é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando logradouro é vazio")
  void testLogradouroNaoPodeSerVazio() {
    Endereco endereco = Endereco.builder()
        .logradouro("")
        .numero("123")
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")
            && v.getMessage().equals("Logradouro é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando logradouro é apenas espaços")
  void testLogradouroNaoPodeSerApenasEspacos() {
    Endereco endereco = Endereco.builder()
        .logradouro("   ")
        .numero("123")
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")
            && v.getMessage().equals("Logradouro é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando número é nulo")
  void testNumeroNaoPodeSerNulo() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero(null)
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("numero")
            && v.getMessage().equals("Número é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando número é vazio")
  void testNumeroNaoPodeSerVazio() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero("")
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("numero")
            && v.getMessage().equals("Número é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando bairro é nulo")
  void testBairroNaoPodeSerNulo() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro(null)
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("bairro")
            && v.getMessage().equals("Bairro é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando bairro é vazio")
  void testBairroNaoPodeSerVazio() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("bairro")
            && v.getMessage().equals("Bairro é obrigatório")));
  }

  @Test
  @DisplayName("Deve criar endereço válido sem violações")
  void testCriacaoEnderecoValido() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertTrue(violations.isEmpty());
    assertEquals("Rua Exemplo", endereco.getLogradouro());
    assertEquals("123", endereco.getNumero());
    assertEquals("Centro", endereco.getBairro());
  }

  @Test
  @DisplayName("Deve criar endereço válido com referência")
  void testCriacaoEnderecoComReferencia() {
    Endereco endereco = Endereco.builder()
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .referencia("Próximo ao mercado")
        .build();

    Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

    assertTrue(violations.isEmpty());
    assertEquals("Próximo ao mercado", endereco.getReferencia());
  }

  @Test
  @DisplayName("Deve criar endereço usando padrão Builder")
  void testBuilderPattern() {
    Endereco endereco = Endereco.builder()
        .id(1L)
        .logradouro("Avenida Principal")
        .numero("456")
        .bairro("Bairro Novo")
        .referencia("Ao lado da escola")
        .build();

    assertNotNull(endereco);
    assertEquals(1L, endereco.getId());
    assertEquals("Avenida Principal", endereco.getLogradouro());
    assertEquals("456", endereco.getNumero());
    assertEquals("Bairro Novo", endereco.getBairro());
    assertEquals("Ao lado da escola", endereco.getReferencia());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Endereco endereco = new Endereco();
    endereco.setId(10L);
    endereco.setLogradouro("Rua Teste");
    endereco.setNumero("789");
    endereco.setBairro("Bairro Teste");
    endereco.setReferencia("Referência Teste");

    assertEquals(10L, endereco.getId());
    assertEquals("Rua Teste", endereco.getLogradouro());
    assertEquals("789", endereco.getNumero());
    assertEquals("Bairro Teste", endereco.getBairro());
    assertEquals("Referência Teste", endereco.getReferencia());
  }
}
