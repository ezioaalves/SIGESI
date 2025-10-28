package com.sigesi.sigesi.cemiterios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.enderecos.Endereco;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitários para a entidade Cemiterio.
 */
@DisplayName("Cemiterio Entity Tests")
class CemiterioEntityTest {

  private Validator validator;
  private Endereco enderecoValido;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    enderecoValido = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();
  }

  @Test
  @DisplayName("Deve lançar violação quando nome é nulo")
  void testNomeNaoPodeSerNulo() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome(null)
        .endereco(enderecoValido)
        .build();

    Set<ConstraintViolation<Cemiterio>> violations = validator.validate(cemiterio);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")
            && v.getMessage().equals("Nome é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando nome é vazio")
  void testNomeNaoPodeSerVazio() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("")
        .endereco(enderecoValido)
        .build();

    Set<ConstraintViolation<Cemiterio>> violations = validator.validate(cemiterio);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")
            && v.getMessage().equals("Nome é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando nome é apenas espaços")
  void testNomeNaoPodeSerApenasEspacos() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("   ")
        .endereco(enderecoValido)
        .build();

    Set<ConstraintViolation<Cemiterio>> violations = validator.validate(cemiterio);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")
            && v.getMessage().equals("Nome é obrigatório")));
  }

  @Test
  @DisplayName("Deve lançar violação quando endereço é nulo")
  void testEnderecoNaoPodeSerNulo() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("Cemitério Central")
        .endereco(null)
        .build();

    Set<ConstraintViolation<Cemiterio>> violations = validator.validate(cemiterio);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("endereco")
            && v.getMessage().equals("Endereço é obrigatório")));
  }

  @Test
  @DisplayName("Deve criar cemitério válido sem violações")
  void testCriacaoCemiterioValido() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("Cemitério Central")
        .endereco(enderecoValido)
        .build();

    Set<ConstraintViolation<Cemiterio>> violations = validator.validate(cemiterio);

    assertTrue(violations.isEmpty());
    assertEquals("Cemitério Central", cemiterio.getNome());
    assertNotNull(cemiterio.getEndereco());
    assertEquals(1L, cemiterio.getEndereco().getId());
  }

  @Test
  @DisplayName("Deve criar cemitério usando padrão Builder")
  void testBuilderPattern() {
    Cemiterio cemiterio = Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Municipal")
        .endereco(enderecoValido)
        .build();

    assertNotNull(cemiterio);
    assertEquals(1L, cemiterio.getId());
    assertEquals("Cemitério Municipal", cemiterio.getNome());
    assertNotNull(cemiterio.getEndereco());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Cemiterio cemiterio = new Cemiterio();
    cemiterio.setId(10L);
    cemiterio.setNome("Cemitério Teste");
    cemiterio.setEndereco(enderecoValido);

    assertEquals(10L, cemiterio.getId());
    assertEquals("Cemitério Teste", cemiterio.getNome());
    assertEquals(enderecoValido, cemiterio.getEndereco());
  }
}
