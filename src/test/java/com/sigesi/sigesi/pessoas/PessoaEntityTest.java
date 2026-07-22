package com.sigesi.sigesi.pessoas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * Testes unitários para a entidade Pessoa.
 */
@DisplayName("Pessoa Entity Tests")
class PessoaEntityTest {

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
    Pessoa pessoa = Pessoa.builder()
        .nome(null)
        .cpf("123.456.789-00")
        .sexo(SexoEnum.MASCULINO)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
  }

  @Test
  @DisplayName("Deve lançar violação quando nome é vazio")
  void testNomeNaoPodeSerVazio() {
    Pessoa pessoa = Pessoa.builder()
        .nome("")
        .cpf("123.456.789-00")
        .sexo(SexoEnum.MASCULINO)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
  }

  @Test
  @DisplayName("Deve lançar violação quando CPF é nulo")
  void testCpfNaoPodeSerNulo() {
    Pessoa pessoa = Pessoa.builder()
        .nome("João Silva")
        .cpf(null)
        .sexo(SexoEnum.MASCULINO)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
  }

  @Test
  @DisplayName("Deve lançar violação quando CPF é vazio")
  void testCpfNaoPodeSerVazio() {
    Pessoa pessoa = Pessoa.builder()
        .nome("João Silva")
        .cpf("")
        .sexo(SexoEnum.MASCULINO)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
  }

  @Test
  @DisplayName("Deve lançar violação quando sexo é nulo")
  void testSexoNaoPodeSerNulo() {
    Pessoa pessoa = Pessoa.builder()
        .nome("João Silva")
        .cpf("123.456.789-00")
        .sexo(null)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("sexo")));
  }

  @Test
  @DisplayName("Deve criar pessoa válida sem violações")
  void testCriacaoPessoaValida() {
    Pessoa pessoa = Pessoa.builder()
        .nome("João Silva")
        .cpf("123.456.789-00")
        .sexo(SexoEnum.MASCULINO)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertTrue(violations.isEmpty());
    assertEquals("João Silva", pessoa.getNome());
    assertEquals("123.456.789-00", pessoa.getCpf());
    assertEquals(SexoEnum.MASCULINO, pessoa.getSexo());
    assertNull(pessoa.getEndereco());
  }

  @Test
  @DisplayName("Deve criar pessoa válida com endereço")
  void testCriacaoPessoaComEndereco() {
    Pessoa pessoa = Pessoa.builder()
        .nome("Maria Silva")
        .cpf("987.654.321-00")
        .sexo(SexoEnum.FEMININO)
        .endereco(enderecoValido)
        .build();

    Set<ConstraintViolation<Pessoa>> violations = validator.validate(pessoa);

    assertTrue(violations.isEmpty());
    assertEquals(enderecoValido, pessoa.getEndereco());
  }

  @Test
  @DisplayName("Deve criar pessoa com todos os valores de sexo")
  void testCriacaoPessoaComTodosSexos() {
    Pessoa masculino = Pessoa.builder()
        .nome("João")
        .cpf("111.111.111-11")
        .sexo(SexoEnum.MASCULINO)
        .build();

    Pessoa feminino = Pessoa.builder()
        .nome("Maria")
        .cpf("222.222.222-22")
        .sexo(SexoEnum.FEMININO)
        .build();

    Pessoa outro = Pessoa.builder()
        .nome("Alex")
        .cpf("333.333.333-33")
        .sexo(SexoEnum.OUTRO)
        .build();

    assertTrue(validator.validate(masculino).isEmpty());
    assertTrue(validator.validate(feminino).isEmpty());
    assertTrue(validator.validate(outro).isEmpty());
  }

  @Test
  @DisplayName("Deve criar pessoa usando padrão Builder")
  void testBuilderPattern() {
    Pessoa pessoa = Pessoa.builder()
        .id(1L)
        .nome("José Santos")
        .cpf("456.789.123-00")
        .sexo(SexoEnum.MASCULINO)
        .endereco(enderecoValido)
        .build();

    assertNotNull(pessoa);
    assertEquals(1L, pessoa.getId());
    assertEquals("José Santos", pessoa.getNome());
    assertEquals("456.789.123-00", pessoa.getCpf());
    assertEquals(SexoEnum.MASCULINO, pessoa.getSexo());
    assertEquals(enderecoValido, pessoa.getEndereco());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Pessoa pessoa = new Pessoa();
    pessoa.setId(10L);
    pessoa.setNome("Ana Costa");
    pessoa.setCpf("321.654.987-00");
    pessoa.setSexo(SexoEnum.FEMININO);
    pessoa.setEndereco(enderecoValido);

    assertEquals(10L, pessoa.getId());
    assertEquals("Ana Costa", pessoa.getNome());
    assertEquals("321.654.987-00", pessoa.getCpf());
    assertEquals(SexoEnum.FEMININO, pessoa.getSexo());
    assertEquals(enderecoValido, pessoa.getEndereco());
  }
}
