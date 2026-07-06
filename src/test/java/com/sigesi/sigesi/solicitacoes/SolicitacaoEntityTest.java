package com.sigesi.sigesi.solicitacoes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitarios para a entidade Solicitacao.
 */
@DisplayName("Solicitacao Entity Tests")
class SolicitacaoEntityTest {

  private Validator validator;
  private Usuario autor;
  private Endereco local;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    autor = Usuario.builder().id(1L).email("test@test.com").build();
    local = Endereco.builder()
        .id(1L).logradouro("Rua A").numero("1").bairro("Centro")
        .build();
  }

  @Test
  @DisplayName("Deve criar entidade valida com campos obrigatorios")
  void testCriacaoValida() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("Descricao do problema")
        .autor(autor)
        .local(local)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando body e nulo")
  void testBodyNulo() {
    Solicitacao solicitacao = Solicitacao.builder()
        .autor(autor)
        .local(local)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("body")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando body e vazio")
  void testBodyVazio() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("")
        .autor(autor)
        .local(local)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("body")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando body e apenas espacos")
  void testBodyApenasEspacos() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("   ")
        .autor(autor)
        .local(local)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("body")));
  }

  @Test
  @DisplayName("Deve permitir autor nulo")
  void testAutorNuloPermitido() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("Descricao")
        .local(local)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando local e nulo")
  void testLocalNulo() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("Descricao")
        .autor(autor)
        .data(LocalDate.now())
        .build();

    Set<ConstraintViolation<Solicitacao>> violations = validator.validate(solicitacao);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("local")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Solicitacao solicitacao = Solicitacao.builder()
        .id(1L)
        .body("Descricao")
        .autor(autor)
        .local(local)
        .assunto(SolicitacaoAssunto.BURACO)
        .status(SolicitacaoStatus.ABERTA)
        .data(LocalDate.now())
        .build();

    assertNotNull(solicitacao);
    assertNotNull(solicitacao.getAutor());
    assertNotNull(solicitacao.getLocal());
  }

  @Test
  @DisplayName("Deve definir data via PrePersist quando nula")
  void testOnCreateSetsDataQuandoNula() {
    Solicitacao solicitacao = Solicitacao.builder()
        .body("Descricao")
        .autor(autor)
        .local(local)
        .build();

    solicitacao.onCreate();

    assertNotNull(solicitacao.getData());
  }

  @Test
  @DisplayName("Deve manter data existente via PrePersist")
  void testOnCreateNaoSobrescreveData() {
    LocalDate dataAnterior = LocalDate.of(2024, 1, 1);
    Solicitacao solicitacao = Solicitacao.builder()
        .body("Descricao")
        .autor(autor)
        .local(local)
        .data(dataAnterior)
        .build();

    solicitacao.onCreate();

    assertTrue(solicitacao.getData().isEqual(dataAnterior));
  }
}
