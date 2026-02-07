package com.sigesi.sigesi.comentarios;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.usuarios.Usuario;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitarios para a entidade Comentario.
 */
@DisplayName("Comentario Entity Tests")
class ComentarioEntityTest {

  private Validator validator;
  private Demanda demanda;
  private Usuario autor;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    demanda = Demanda.builder().id(1L).build();
    autor = Usuario.builder().id(1L).email("test@test.com").build();
  }

  @Test
  @DisplayName("Deve criar entidade valida sem violacoes")
  void testCriacaoValida() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .texto("Comentario de teste")
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve lancar violacao quando demanda e nula")
  void testDemandaNula() {
    Comentario comentario = Comentario.builder()
        .autor(autor)
        .texto("Comentario de teste")
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("demanda")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando autor e nulo")
  void testAutorNulo() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .texto("Comentario de teste")
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("autor")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando texto e nulo")
  void testTextoNulo() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("texto")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando texto e vazio")
  void testTextoVazio() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .texto("")
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("texto")));
  }

  @Test
  @DisplayName("Deve lancar violacao quando texto e apenas espacos")
  void testTextoApenasEspacos() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .texto("   ")
        .build();

    Set<ConstraintViolation<Comentario>> violations = validator.validate(comentario);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("texto")));
  }

  @Test
  @DisplayName("Deve criar entidade usando padrao Builder")
  void testBuilderPattern() {
    Comentario comentario = Comentario.builder()
        .id(1L)
        .demanda(demanda)
        .autor(autor)
        .texto("Texto")
        .build();

    assertNotNull(comentario);
    assertNotNull(comentario.getDemanda());
    assertNotNull(comentario.getAutor());
  }

  @Test
  @DisplayName("Deve definir criadoEm via PrePersist quando nulo")
  void testOnCreateSetsCriadoEmQuandoNulo() {
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .texto("Texto")
        .build();

    comentario.onCreate();

    assertNotNull(comentario.getCriadoEm());
  }

  @Test
  @DisplayName("Deve manter criadoEm existente via PrePersist")
  void testOnCreateNaoSobrescreveCriadoEm() {
    LocalDateTime dataAnterior = LocalDateTime.of(2024, 1, 1, 10, 0);
    Comentario comentario = Comentario.builder()
        .demanda(demanda)
        .autor(autor)
        .texto("Texto")
        .criadoEm(dataAnterior)
        .build();

    comentario.onCreate();

    assertTrue(comentario.getCriadoEm().isEqual(dataAnterior));
  }
}
