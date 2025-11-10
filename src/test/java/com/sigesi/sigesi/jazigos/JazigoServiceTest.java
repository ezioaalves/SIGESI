package com.sigesi.sigesi.jazigos;

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

import com.sigesi.sigesi.cemiterios.Cemiterio;
import com.sigesi.sigesi.cemiterios.CemiterioService;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.jazigos.dtos.JazigoCreateDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoResponseDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoUpdateDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("JazigoService Tests")
class JazigoServiceTest {

  @Mock
  private JazigoRepository jazigoRepository;

  @Mock
  private CemiterioService cemiterioService;

  @Mock
  private JazigoMapper jazigoMapper;

  @InjectMocks
  private JazigoService jazigoService;

  private Cemiterio cemiterioEntity;
  private JazigoCreateDTO jazigoCreateDTO;

  @BeforeEach
  void setUp() {
    Endereco endereco = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    cemiterioEntity = Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Central")
        .endereco(endereco)
        .build();

    jazigoCreateDTO = new JazigoCreateDTO();
    jazigoCreateDTO.setCemiterio(1L);
    jazigoCreateDTO.setQuadra(1);
    jazigoCreateDTO.setRua("A");
    jazigoCreateDTO.setLote("10");
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há jazigos")
  void testGetAllRetornaListaVazia() {
    when(jazigoRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<JazigoResponseDTO> resultado = jazigoService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(jazigoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com jazigos existentes")
  void testGetAllRetornaListaComJazigos() {
    Jazigo j1 = mock(Jazigo.class);
    Jazigo j2 = mock(Jazigo.class);
    Jazigo j3 = mock(Jazigo.class);

    when(jazigoRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(j1, j2, j3));
    when(jazigoMapper.toDto(any())).thenReturn(mock(JazigoResponseDTO.class));

    List<JazigoResponseDTO> resultado = jazigoService.getAll();

    assertEquals(3, resultado.size());
    verify(jazigoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar jazigo quando buscar por ID existente")
  void testGetJazigoByIdComSucesso() {
    Jazigo jazigo = mock(Jazigo.class);
    JazigoResponseDTO dto = new JazigoResponseDTO();
    dto.setId(1L);

    when(jazigoRepository.findById(1L)).thenReturn(Optional.of(jazigo));
    when(jazigoMapper.toDto(jazigo)).thenReturn(dto);

    JazigoResponseDTO resultado = jazigoService.getJazigoById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por ID inexistente")
  void testGetJazigoByIdLancaExcecaoQuandoNaoEncontrado() {
    when(jazigoRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      jazigoService.getJazigoById(999L);
    });

    assertTrue(exception.getMessage().contains("Jazigo não encontrado"));
  }

  @Test
  @DisplayName("Deve criar jazigo com cemitério válido")
  void testCreateJazigoComSucesso() {
    Jazigo jazigoEntity = mock(Jazigo.class);
    JazigoResponseDTO dto = mock(JazigoResponseDTO.class);

    when(cemiterioService.getCemiterioEntityById(1L)).thenReturn(cemiterioEntity);
    when(jazigoMapper.toEntity(jazigoCreateDTO)).thenReturn(jazigoEntity);
    when(jazigoMapper.toDto(jazigoEntity)).thenReturn(dto);

    JazigoResponseDTO resultado = jazigoService.createJazigo(jazigoCreateDTO);

    assertNotNull(resultado);
    verify(cemiterioService, times(1)).getCemiterioEntityById(1L);
    verify(jazigoRepository, times(1)).save(jazigoEntity);
  }

  @Test
  @DisplayName("Deve atualizar jazigo com sucesso")
  void testUpdateJazigoComSucesso() {
    Jazigo jazigo = mock(Jazigo.class);
    JazigoUpdateDTO updateDTO = new JazigoUpdateDTO();
    updateDTO.setCemiterio(1L);
    updateDTO.setQuadra(2);
    JazigoResponseDTO dto = mock(JazigoResponseDTO.class);

    when(jazigoRepository.findById(1L)).thenReturn(Optional.of(jazigo));
    when(cemiterioService.getCemiterioEntityById(anyLong())).thenReturn(cemiterioEntity);
    when(jazigoMapper.toDto(jazigo)).thenReturn(dto);

    JazigoResponseDTO resultado = jazigoService.updateJazigo(1L, updateDTO);

    assertNotNull(resultado);
    verify(jazigoRepository, times(1)).save(jazigo);
    verify(cemiterioService, times(1)).getCemiterioEntityById(1L);
  }

  @Test
  @DisplayName("Deve deletar jazigo com sucesso")
  void testDeleteJazigoComSucesso() {
    Jazigo jazigo = mock(Jazigo.class);
    when(jazigoRepository.findById(1L)).thenReturn(Optional.of(jazigo));

    jazigoService.deleteJazigo(1L);

    verify(jazigoRepository, times(1)).delete(jazigo);
  }

  @Test
  @DisplayName("Deve lançar exceção 404 ao deletar jazigo inexistente")
  void testDeleteJazigoLancaExcecaoQuandoNaoEncontrado() {
    when(jazigoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> jazigoService.deleteJazigo(999L));
    verify(jazigoRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade jazigo quando buscar por ID existente")
  void testGetJazigoEntityByIdComSucesso() {
    Jazigo jazigo = Jazigo.builder()
        .id(1L)
        .cemiterio(cemiterioEntity)
        .quadra(1)
        .rua("A")
        .lote("10")
        .build();

    when(jazigoRepository.findById(1L)).thenReturn(Optional.of(jazigo));

    Jazigo resultado = jazigoService.getJazigoEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("A", resultado.getRua());
  }
}
