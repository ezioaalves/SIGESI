package com.sigesi.sigesi.materiais;

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
 * Testes unitarios para a entidade Material.
 */
@DisplayName("Material Entity Tests")
class MaterialEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve criar entidade valida sem violacoes")
  void testCriacaoValida() {
    Material material = Material.builder()
        .nome("Cimento")
        .preco(50.0)
        .build();

    Set<ConstraintViolation<Material>> violations = validator.validate(material);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando nome e nulo")
  void testNomeNulo() {
    Material material = Material.builder()
        .preco(50.0)
        .build();

    Set<ConstraintViolation<Material>> violations = validator.validate(material);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando nome e vazio")
  void testNomeVazio() {
    Material material = Material.builder()
        .nome("")
        .preco(50.0)
        .build();

    Set<ConstraintViolation<Material>> violations = validator.validate(material);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando nome e apenas espacos")
  void testNomeApenasEspacos() {
    Material material = Material.builder()
        .nome("   ")
        .preco(50.0)
        .build();

    Set<ConstraintViolation<Material>> violations = validator.validate(material);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando preco e nulo")
  void testPrecoNulo() {
    Material material = Material.builder()
        .nome("Cimento")
        .build();

    Set<ConstraintViolation<Material>> violations = validator.validate(material);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Material material = Material.builder()
        .id(1L)
        .nome("Areia")
        .preco(30.0)
        .build();

    assertNotNull(material);
    assertEquals(1L, material.getId());
    assertEquals("Areia", material.getNome());
    assertEquals(30.0, material.getPreco());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Material material = new Material();
    material.setId(10L);
    material.setNome("Brita");
    material.setPreco(45.0);

    assertEquals(10L, material.getId());
    assertEquals("Brita", material.getNome());
    assertEquals(45.0, material.getPreco());
  }
}
