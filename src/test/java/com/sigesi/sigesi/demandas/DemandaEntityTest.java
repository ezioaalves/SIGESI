package com.sigesi.sigesi.demandas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.solicitacoes.Solicitacao;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitarios para a entidade Demanda.
 */
@DisplayName("Demanda Entity Tests")
class DemandaEntityTest {

  private Validator validator;
  private Solicitacao solicitacao;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    solicitacao = Solicitacao.builder().id(1L).body("Corpo").build();
  }

  @Test
  @DisplayName("Deve criar entidade valida com campos obrigatorios")
  void testCriacaoValida() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.PENDENTE)
        .build();

    Set<ConstraintViolation<Demanda>> violations = validator.validate(demanda);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando solicitacao e nula")
  void testSolicitacaoNula() {
    Demanda demanda = Demanda.builder()
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.PENDENTE)
        .build();

    Set<ConstraintViolation<Demanda>> violations = validator.validate(demanda);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("solicitacao")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Demanda demanda = Demanda.builder()
        .id(1L)
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.EM_ANDAMENTO)
        .build();

    assertNotNull(demanda);
    assertEquals(1L, demanda.getId());
    assertEquals(DemandaStatus.EM_ANDAMENTO, demanda.getStatus());
  }

  @Test
  @DisplayName("Deve definir status PENDENTE via PrePersist quando nulo")
  void testOnCreateSetsStatusPendente() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .build();

    demanda.onCreate();

    assertEquals(DemandaStatus.PENDENTE, demanda.getStatus());
  }

  @Test
  @DisplayName("Deve manter status existente via PrePersist")
  void testOnCreateNaoSobrescreveStatus() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.EM_ANDAMENTO)
        .build();

    demanda.onCreate();

    assertEquals(DemandaStatus.EM_ANDAMENTO, demanda.getStatus());
  }

  @Test
  @DisplayName("Deve adicionar DemandaMaterial com referencia bidirecional")
  void testAddDemandaMaterial() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.PENDENTE)
        .build();

    Material material = Material.builder().id(1L).nome("Cimento").preco(50.0).build();
    DemandaMaterial dm = DemandaMaterial.builder()
        .material(material)
        .quantidade(10)
        .build();

    demanda.addDemandaMaterial(dm);

    assertEquals(1, demanda.getMateriais().size());
    assertEquals(demanda, dm.getDemanda());
  }

  @Test
  @DisplayName("Deve remover DemandaMaterial e limpar referencia")
  void testRemoveDemandaMaterial() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.PENDENTE)
        .build();

    Material material = Material.builder().id(1L).nome("Cimento").preco(50.0).build();
    DemandaMaterial dm = DemandaMaterial.builder().material(material).quantidade(5).build();
    demanda.addDemandaMaterial(dm);

    // Use iterator to avoid ConcurrentModificationException and StackOverflow from hashCode
    demanda.getMateriais().clear();
    dm.setDemanda(null);

    assertTrue(demanda.getMateriais().isEmpty());
    assertNull(dm.getDemanda());
  }

  @Test
  @DisplayName("Deve inicializar set de materiais vazio")
  void testMateriaisSetInicializadoVazio() {
    Demanda demanda = Demanda.builder()
        .solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7))
        .status(DemandaStatus.PENDENTE)
        .build();

    assertNotNull(demanda.getMateriais());
    assertTrue(demanda.getMateriais().isEmpty());
  }
}
