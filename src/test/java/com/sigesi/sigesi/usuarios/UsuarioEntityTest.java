package com.sigesi.sigesi.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Testes unitários para a entidade Usuario.
 */
@DisplayName("Usuario Entity Tests")
class UsuarioEntityTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve criar usuário válido sem violações")
  void testCriacaoUsuarioValido() {
    Usuario usuario = Usuario.builder()
        .email("usuario@example.com")
        .name("João Silva")
        .pictureUrl("https://example.com/picture.jpg")
        .provider("google")
        .ativo(true)
        .build();

    Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

    assertTrue(violations.isEmpty());
    assertEquals("usuario@example.com", usuario.getEmail());
    assertEquals("João Silva", usuario.getName());
    assertEquals("https://example.com/picture.jpg", usuario.getPictureUrl());
    assertEquals("google", usuario.getProvider());
    assertTrue(usuario.getAtivo());
  }

  @Test
  @DisplayName("Deve criar usuário inativo")
  void testCriacaoUsuarioInativo() {
    Usuario usuario = Usuario.builder()
        .email("usuario@example.com")
        .name("Maria Santos")
        .ativo(false)
        .build();

    Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

    assertTrue(violations.isEmpty());
    assertEquals(false, usuario.getAtivo());
  }

  @Test
  @DisplayName("Deve criar usuário usando padrão Builder")
  void testBuilderPattern() {
    Usuario usuario = Usuario.builder()
        .id(1L)
        .email("teste@example.com")
        .name("Teste Usuário")
        .pictureUrl("https://example.com/pic.jpg")
        .provider("google")
        .ativo(true)
        .build();

    assertNotNull(usuario);
    assertEquals(1L, usuario.getId());
    assertEquals("teste@example.com", usuario.getEmail());
    assertEquals("Teste Usuário", usuario.getName());
    assertEquals("https://example.com/pic.jpg", usuario.getPictureUrl());
    assertEquals("google", usuario.getProvider());
    assertTrue(usuario.getAtivo());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setEmail("lombok@example.com");
    usuario.setName("Lombok User");
    usuario.setPictureUrl("https://example.com/lombok.jpg");
    usuario.setProvider("google");
    usuario.setAtivo(true);

    assertEquals(10L, usuario.getId());
    assertEquals("lombok@example.com", usuario.getEmail());
    assertEquals("Lombok User", usuario.getName());
    assertEquals("https://example.com/lombok.jpg", usuario.getPictureUrl());
    assertEquals("google", usuario.getProvider());
    assertTrue(usuario.getAtivo());
  }

  @Test
  @DisplayName("Deve permitir usuário com campos nulos")
  void testCriacaoUsuarioComCamposNulos() {
    Usuario usuario = Usuario.builder()
        .email(null)
        .name(null)
        .pictureUrl(null)
        .provider(null)
        .ativo(null)
        .build();

    Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

    // Usuario não tem validações @NotNull, então não deve ter violações
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve alternar estado ativo do usuário")
  void testAlternarEstadoAtivo() {
    Usuario usuario = Usuario.builder()
        .email("usuario@example.com")
        .ativo(true)
        .build();

    assertEquals(true, usuario.getAtivo());

    usuario.setAtivo(!usuario.getAtivo());
    assertEquals(false, usuario.getAtivo());

    usuario.setAtivo(!usuario.getAtivo());
    assertEquals(true, usuario.getAtivo());
  }
}
