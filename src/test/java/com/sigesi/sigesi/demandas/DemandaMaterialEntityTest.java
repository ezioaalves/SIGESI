package com.sigesi.sigesi.demandas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.materiais.Material;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitarios para a entidade DemandaMaterial.
 */
@DisplayName("DemandaMaterial Entity Tests")
class DemandaMaterialEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve criar entidade valida sem violacoes")
  void testCriacaoValida() {
    DemandaMaterial dm = DemandaMaterial.builder()
        .quantidade(10)
        .build();

    Set<ConstraintViolation<DemandaMaterial>> violations = validator.validate(dm);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando quantidade e nula")
  void testQuantidadeNula() {
    DemandaMaterial dm = DemandaMaterial.builder().build();

    Set<ConstraintViolation<DemandaMaterial>> violations = validator.validate(dm);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("quantidade")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Material material = Material.builder().id(1L).nome("Cimento").preco(50.0).build();
    Demanda demanda = Demanda.builder().id(1L).build();

    DemandaMaterial dm = DemandaMaterial.builder()
        .id(1L)
        .demanda(demanda)
        .material(material)
        .quantidade(5)
        .build();

    assertNotNull(dm);
    assertEquals(1L, dm.getId());
    assertEquals(5, dm.getQuantidade());
    assertNotNull(dm.getDemanda());
    assertNotNull(dm.getMaterial());
  }

  @Test
  @DisplayName("Deve definir relacionamentos corretamente")
  void testRelacionamentos() {
    Material material = Material.builder().id(2L).nome("Areia").preco(30.0).build();
    Demanda demanda = Demanda.builder().id(3L).build();

    DemandaMaterial dm = new DemandaMaterial();
    dm.setDemanda(demanda);
    dm.setMaterial(material);
    dm.setQuantidade(20);

    assertEquals(demanda, dm.getDemanda());
    assertEquals(material, dm.getMaterial());
    assertEquals(20, dm.getQuantidade());
  }
}
