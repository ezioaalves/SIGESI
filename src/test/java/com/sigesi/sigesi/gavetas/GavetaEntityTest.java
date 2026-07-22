package com.sigesi.sigesi.gavetas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sigesi.sigesi.cemiterios.Cemiterio;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.jazigos.Jazigo;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.SexoEnum;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Testes unitários para a entidade Gaveta.
 */
@DisplayName("Gaveta Entity Tests")
class GavetaEntityTest {

  private Validator validator;
  private Jazigo jazigoValido;
  private Pessoa pessoaValida;

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

    Cemiterio cemiterio = Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Central")
        .endereco(endereco)
        .build();

    jazigoValido = Jazigo.builder()
        .id(1L)
        .cemiterio(cemiterio)
        .quadra(1)
        .rua("A")
        .lote("10")
        .build();

    pessoaValida = Pessoa.builder()
        .id(1L)
        .nome("João Silva")
        .cpf("123.456.789-00")
        .sexo(SexoEnum.MASCULINO)
        .build();
  }

  @Test
  @DisplayName("Deve lançar violação quando jazigo é nulo")
  void testJazigoNaoPodeSerNulo() {
    Gaveta gaveta = Gaveta.builder()
        .jazigo(null)
        .numero(1)
        .build();

    Set<ConstraintViolation<Gaveta>> violations = validator.validate(gaveta);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("jazigo")
            && v.getMessage().equals("Jazigo é obrigatório")));
  }

  @Test
  @DisplayName("Deve criar gaveta válida sem violações")
  void testCriacaoGavetaValida() {
    Gaveta gaveta = Gaveta.builder()
        .jazigo(jazigoValido)
        .numero(1)
        .build();

    Set<ConstraintViolation<Gaveta>> violations = validator.validate(gaveta);

    assertTrue(violations.isEmpty());
    assertEquals(jazigoValido, gaveta.getJazigo());
    assertEquals(1, gaveta.getNumero());
    assertNull(gaveta.getOcupante());
  }

  @Test
  @DisplayName("Deve criar gaveta válida com ocupante")
  void testCriacaoGavetaComOcupante() {
    Gaveta gaveta = Gaveta.builder()
        .jazigo(jazigoValido)
        .numero(1)
        .ocupante(pessoaValida)
        .build();

    Set<ConstraintViolation<Gaveta>> violations = validator.validate(gaveta);

    assertTrue(violations.isEmpty());
    assertEquals(pessoaValida, gaveta.getOcupante());
  }

  @Test
  @DisplayName("Deve criar gaveta válida sem número")
  void testCriacaoGavetaSemNumero() {
    Gaveta gaveta = Gaveta.builder()
        .jazigo(jazigoValido)
        .build();

    Set<ConstraintViolation<Gaveta>> violations = validator.validate(gaveta);

    assertTrue(violations.isEmpty());
    assertNull(gaveta.getNumero());
  }

  @Test
  @DisplayName("Deve criar gaveta usando padrão Builder")
  void testBuilderPattern() {
    Gaveta gaveta = Gaveta.builder()
        .id(1L)
        .jazigo(jazigoValido)
        .numero(5)
        .ocupante(pessoaValida)
        .build();

    assertNotNull(gaveta);
    assertEquals(1L, gaveta.getId());
    assertEquals(jazigoValido, gaveta.getJazigo());
    assertEquals(5, gaveta.getNumero());
    assertEquals(pessoaValida, gaveta.getOcupante());
  }

  @Test
  @DisplayName("Deve funcionar getters e setters do Lombok")
  void testLombokGettersSetters() {
    Gaveta gaveta = new Gaveta();
    gaveta.setId(10L);
    gaveta.setJazigo(jazigoValido);
    gaveta.setNumero(3);
    gaveta.setOcupante(pessoaValida);

    assertEquals(10L, gaveta.getId());
    assertEquals(jazigoValido, gaveta.getJazigo());
    assertEquals(3, gaveta.getNumero());
    assertEquals(pessoaValida, gaveta.getOcupante());
  }

  @Test
  @DisplayName("Deve permitir gaveta sem ocupante")
  void testGavetaSemOcupante() {
    Gaveta gaveta = Gaveta.builder()
        .jazigo(jazigoValido)
        .numero(2)
        .ocupante(null)
        .build();

    Set<ConstraintViolation<Gaveta>> violations = validator.validate(gaveta);

    assertTrue(violations.isEmpty());
    assertNull(gaveta.getOcupante());
  }
}
