package com.sigesi.sigesi.cemiterios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

import com.sigesi.sigesi.cemiterios.dtos.CemiterioCreateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioResponseDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioUpdateDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("CemiterioService Tests")
class CemiterioServiceTest {

  @Mock
  private CemiterioRepository cemiterioRepository;

  @Mock
  private EnderecoService enderecoService;

  @Mock
  private CemiterioMapper cemiterioMapper;

  @InjectMocks
  private CemiterioService cemiterioService;

  private CemiterioCreateDTO cemiterioCreateDTO;
  private Endereco enderecoEntity;
  private EnderecoResponseDTO enderecoDTO;

  @BeforeEach
  void setUp() {
    enderecoEntity = Endereco.builder().id(1L).logradouro("Rua Exemplo").numero("123").bairro("Centro").build();
    enderecoDTO = new EnderecoResponseDTO(1L, "Rua Exemplo", "123", "Centro", null);
    cemiterioCreateDTO = new CemiterioCreateDTO("Cemitério Central", enderecoDTO.getId());
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há cemitérios")
  void testGetAllRetornaListaVazia() {
    when(cemiterioRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<CemiterioResponseDTO> resultado = cemiterioService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(cemiterioRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com cemitérios existentes")
  void testGetAllRetornaListaComCemiterios() {
    Cemiterio c1 = mock(Cemiterio.class);
    Cemiterio c2 = mock(Cemiterio.class);
    Cemiterio c3 = mock(Cemiterio.class);

    when(cemiterioRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(c1, c2, c3));
    when(cemiterioMapper.toDto(any())).thenReturn(mock(CemiterioResponseDTO.class));

    List<CemiterioResponseDTO> resultado = cemiterioService.getAll();

    assertEquals(3, resultado.size());
    verify(cemiterioRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar cemitério quando buscar por ID existente")
  void testGetCemiterioByIdComSucesso() {
    Cemiterio cemiterio = mock(Cemiterio.class);
    CemiterioResponseDTO dto = new CemiterioResponseDTO(1L, "Cemitério Central", enderecoDTO);

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterio));
    when(cemiterioMapper.toDto(cemiterio)).thenReturn(dto);

    CemiterioResponseDTO resultado = cemiterioService.getCemiterioById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Cemitério Central", resultado.getNome());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por ID inexistente")
  void testGetCemiterioByIdLancaExcecaoQuandoNaoEncontrado() {
    when(cemiterioRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      cemiterioService.getCemiterioById(999L);
    });

    assertTrue(exception.getMessage().contains("Cemiterio não econtrado"));
  }

  @Test
  @DisplayName("Deve criar cemitério com endereço válido")
  void testCreateCemiterioComSucesso() {
    Cemiterio cemiterioEntity = mock(Cemiterio.class);
    CemiterioResponseDTO dto = mock(CemiterioResponseDTO.class);

    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(enderecoEntity);
    when(cemiterioMapper.toEntity(cemiterioCreateDTO)).thenReturn(cemiterioEntity);
    when(cemiterioMapper.toDto(cemiterioEntity)).thenReturn(dto);

    CemiterioResponseDTO resultado = cemiterioService.createCemiterio(cemiterioCreateDTO);

    assertNotNull(resultado);
    verify(enderecoService, times(1)).getEnderecoEntityById(1L);
    verify(cemiterioRepository, times(1)).save(cemiterioEntity);
  }

  @Test
  @DisplayName("Deve atualizar cemitério com sucesso")
  void testUpdateCemiterioComSucesso() {
    Cemiterio cemiterio = mock(Cemiterio.class);
    CemiterioUpdateDTO updateDTO = new CemiterioUpdateDTO("Novo Nome", enderecoDTO.getId());
    CemiterioResponseDTO dto = mock(CemiterioResponseDTO.class);

    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterio));
    when(enderecoService.getEnderecoEntityById(anyLong())).thenReturn(enderecoEntity);
    when(cemiterioMapper.toDto(cemiterio)).thenReturn(dto);

    CemiterioResponseDTO resultado = cemiterioService.updateCemiterio(1L, updateDTO);

    assertNotNull(resultado);
    verify(cemiterioRepository, times(1)).save(cemiterio);
    verify(enderecoService, times(1)).getEnderecoEntityById(enderecoDTO.getId());
  }

  @Test
  @DisplayName("Deve deletar cemitério com sucesso")
  void testDeleteCemiterioComSucesso() {
    Cemiterio cemiterio = mock(Cemiterio.class);
    when(cemiterioRepository.findById(1L)).thenReturn(Optional.of(cemiterio));

    cemiterioService.deleteCemiterio(1L);

    verify(cemiterioRepository, times(1)).delete(cemiterio);
  }

  @Test
  @DisplayName("Deve lançar exceção 404 ao deletar cemitério inexistente")
  void testDeleteCemiterioLancaExcecaoQuandoNaoEncontrado() {
    when(cemiterioRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cemiterioService.deleteCemiterio(999L));
    verify(cemiterioRepository, never()).delete(any());
  }
}
