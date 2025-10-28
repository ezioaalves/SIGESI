package com.sigesi.sigesi.cemiterios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;

/**
 * Testes unitários para CemiterioService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CemiterioService Tests")
class CemiterioServiceTest {

  @Mock
  private CemiterioRepository cemiterioRepository;

  @Mock
  private EnderecoService enderecoService;

  @InjectMocks
  private CemiterioService cemiterioService;

  private Cemiterio cemiterioValido;
  private Endereco enderecoValido;

  @BeforeEach
  void setUp() {
    enderecoValido = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    cemiterioValido = Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Central")
        .endereco(enderecoValido)
        .build();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há cemitérios")
  void testGetAllRetornaListaVazia() {
    when(cemiterioRepository.findAll()).thenReturn(List.of());

    List<Cemiterio> resultado = cemiterioService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(cemiterioRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Deve retornar lista com cemitérios existentes")
  void testGetAllRetornaListaComCemiterios() {
    Cemiterio cemiterio2 = Cemiterio.builder()
        .id(2L)
        .nome("Cemitério Municipal")
        .endereco(enderecoValido)
        .build();

    Cemiterio cemiterio3 = Cemiterio.builder()
        .id(3L)
        .nome("Cemitério Parque")
        .endereco(enderecoValido)
        .build();

    when(cemiterioRepository.findAll())
        .thenReturn(Arrays.asList(cemiterioValido, cemiterio2, cemiterio3));

    List<Cemiterio> resultado = cemiterioService.getAll();

    assertNotNull(resultado);
    assertEquals(3, resultado.size());
    verify(cemiterioRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Deve retornar cemitério quando buscar por ID existente")
  void testGetCemiterioByIdComSucesso() {
    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));

    Cemiterio resultado = cemiterioService.getCemiterioById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Cemitério Central", resultado.getNome());
    verify(cemiterioRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Deve lançar exceção quando buscar por ID inexistente")
  void testGetCemiterioByIdLancaExcecaoQuandoNaoEncontrado() {
    when(cemiterioRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      cemiterioService.getCemiterioById(999L);
    });

    assertEquals("Cemitério não encontrado com id 999", exception.getMessage());
    verify(cemiterioRepository, times(1)).findById(999L);
  }

  @Test
  @DisplayName("Deve criar cemitério com endereço válido")
  void testCreateCemiterioComSucesso() {
    when(enderecoService.getEnderecoById(1L)).thenReturn(enderecoValido);
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterioValido);

    Cemiterio resultado = cemiterioService.createCemiterio(cemiterioValido);

    assertNotNull(resultado);
    assertEquals("Cemitério Central", resultado.getNome());
    verify(enderecoService, times(1)).getEnderecoById(1L);
    verify(cemiterioRepository, times(1)).save(cemiterioValido);
  }

  @Test
  @DisplayName("Deve validar que EnderecoService é chamado ao criar cemitério")
  void testCreateCemiterioValidaEnderecoExistente() {
    Endereco endereco = Endereco.builder().id(5L).build();
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("Novo Cemitério")
        .endereco(endereco)
        .build();

    when(enderecoService.getEnderecoById(5L)).thenReturn(endereco);
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterio);

    cemiterioService.createCemiterio(cemiterio);

    verify(enderecoService, times(1)).getEnderecoById(5L);
    verify(cemiterioRepository, times(1)).save(cemiterio);
  }

  @Test
  @DisplayName("Deve criar cemitério sem validar quando endereço é nulo")
  void testCreateCemiterioComEnderecoNulo() {
    Cemiterio cemiterio = Cemiterio.builder()
        .nome("Cemitério Sem Endereço")
        .endereco(null)
        .build();

    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterio);

    cemiterioService.createCemiterio(cemiterio);

    verify(enderecoService, never()).getEnderecoById(anyLong());
    verify(cemiterioRepository, times(1)).save(cemiterio);
  }

  @Test
  @DisplayName("Deve criar cemitério sem validar quando endereço não tem ID")
  void testCreateCemiterioComEnderecoSemId() {
    Endereco enderecoSemId = Endereco.builder()
        .logradouro("Rua Teste")
        .numero("456")
        .bairro("Bairro")
        .build();

    Cemiterio cemiterio = Cemiterio.builder()
        .nome("Cemitério Teste")
        .endereco(enderecoSemId)
        .build();

    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterio);

    cemiterioService.createCemiterio(cemiterio);

    verify(enderecoService, never()).getEnderecoById(anyLong());
    verify(cemiterioRepository, times(1)).save(cemiterio);
  }

  @Test
  @DisplayName("Deve atualizar cemitério com sucesso")
  void testUpdateCemiterioComSucesso() {
    Endereco novoEndereco = Endereco.builder().id(2L).build();
    Cemiterio cemiterioAtualizado = Cemiterio.builder()
        .nome("Novo Nome")
        .endereco(novoEndereco)
        .build();

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));
    when(enderecoService.getEnderecoById(2L)).thenReturn(novoEndereco);
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterioValido);

    Cemiterio resultado = cemiterioService.updateCemiterio(1L, cemiterioAtualizado);

    assertNotNull(resultado);
    assertEquals("Novo Nome", cemiterioValido.getNome());
    assertEquals(novoEndereco, cemiterioValido.getEndereco());
    verify(cemiterioRepository, times(1)).findById(1L);
    verify(enderecoService, times(1)).getEnderecoById(2L);
    verify(cemiterioRepository, times(1)).save(cemiterioValido);
  }

  @Test
  @DisplayName("Deve atualizar apenas nome quando endereço é nulo")
  void testUpdateCemiterioApenasNome() {
    Cemiterio cemiterioAtualizado = Cemiterio.builder()
        .nome("Nome Atualizado")
        .endereco(null)
        .build();

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterioValido);

    cemiterioService.updateCemiterio(1L, cemiterioAtualizado);

    assertEquals("Nome Atualizado", cemiterioValido.getNome());
    verify(enderecoService, never()).getEnderecoById(anyLong());
    verify(cemiterioRepository, times(1)).save(cemiterioValido);
  }

  @Test
  @DisplayName("Deve atualizar endereço quando fornecido")
  void testUpdateCemiterioApenasEndereco() {
    Endereco novoEndereco = Endereco.builder().id(3L).build();
    Cemiterio cemiterioAtualizado = Cemiterio.builder()
        .nome("Cemitério Central")
        .endereco(novoEndereco)
        .build();

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));
    when(enderecoService.getEnderecoById(3L)).thenReturn(novoEndereco);
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterioValido);

    cemiterioService.updateCemiterio(1L, cemiterioAtualizado);

    assertEquals(novoEndereco, cemiterioValido.getEndereco());
    verify(enderecoService, times(1)).getEnderecoById(3L);
  }

  @Test
  @DisplayName("Deve lançar exceção ao atualizar cemitério inexistente")
  void testUpdateCemiterioLancaExcecaoQuandoNaoEncontrado() {
    when(cemiterioRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      cemiterioService.updateCemiterio(999L, cemiterioValido);
    });

    assertEquals("Cemitério não encontrado com id 999", exception.getMessage());
    verify(cemiterioRepository, never()).save(any(Cemiterio.class));
    verify(enderecoService, never()).getEnderecoById(anyLong());
  }

  @Test
  @DisplayName("Deve validar endereço ao atualizar cemitério")
  void testUpdateCemiterioValidaEnderecoExistente() {
    Endereco endereco = Endereco.builder().id(10L).build();
    Cemiterio cemiterioAtualizado = Cemiterio.builder()
        .nome("Cemitério Atualizado")
        .endereco(endereco)
        .build();

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));
    when(enderecoService.getEnderecoById(10L)).thenReturn(endereco);
    when(cemiterioRepository.save(any(Cemiterio.class))).thenReturn(cemiterioValido);

    cemiterioService.updateCemiterio(1L, cemiterioAtualizado);

    verify(enderecoService, times(1)).getEnderecoById(10L);
  }

  @Test
  @DisplayName("Deve deletar cemitério com sucesso")
  void testDeleteCemiterioComSucesso() {
    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterioValido));

    cemiterioService.deleteCemiterio(1L);

    verify(cemiterioRepository, times(1)).findById(1L);
    verify(cemiterioRepository, times(1)).delete(cemiterioValido);
  }

  @Test
  @DisplayName("Deve lançar exceção ao deletar cemitério inexistente")
  void testDeleteCemiterioLancaExcecaoQuandoNaoEncontrado() {
    when(cemiterioRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      cemiterioService.deleteCemiterio(999L);
    });

    assertEquals("Cemitério não encontrado com id 999", exception.getMessage());
    verify(cemiterioRepository, never()).delete(any(Cemiterio.class));
  }
}
