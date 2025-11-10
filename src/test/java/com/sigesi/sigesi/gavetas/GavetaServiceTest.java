package com.sigesi.sigesi.gavetas;

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

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.gavetas.dtos.GavetaCreateDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaResponseDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaUpdateDTO;
import com.sigesi.sigesi.jazigos.Jazigo;
import com.sigesi.sigesi.jazigos.JazigoService;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;

@ExtendWith(MockitoExtension.class)
@DisplayName("GavetaService Tests")
class GavetaServiceTest {

  @Mock
  private GavetaRepository gavetaRepository;

  @Mock
  private GavetaMapper gavetaMapper;

  @Mock
  private JazigoService jazigoService;

  @Mock
  private PessoaService pessoaService;

  @InjectMocks
  private GavetaService gavetaService;

  private GavetaCreateDTO gavetaCreateDTO;
  private Jazigo jazigoEntity;
  private Pessoa pessoaEntity;

  @BeforeEach
  void setUp() {
    jazigoEntity = mock(Jazigo.class);
    pessoaEntity = mock(Pessoa.class);

    gavetaCreateDTO = new GavetaCreateDTO();
    gavetaCreateDTO.setJazigo(1L);
    gavetaCreateDTO.setNumero(1);
    gavetaCreateDTO.setOcupante(1L);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há gavetas")
  void testGetAllRetornaListaVazia() {
    when(gavetaRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<GavetaResponseDTO> resultado = gavetaService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(gavetaRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com gavetas existentes")
  void testGetAllRetornaListaComGavetas() {
    Gaveta g1 = mock(Gaveta.class);
    Gaveta g2 = mock(Gaveta.class);
    Gaveta g3 = mock(Gaveta.class);

    when(gavetaRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(g1, g2, g3));
    when(gavetaMapper.toDto(any())).thenReturn(mock(GavetaResponseDTO.class));

    List<GavetaResponseDTO> resultado = gavetaService.getAll();

    assertEquals(3, resultado.size());
    verify(gavetaRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar gaveta quando buscar por ID existente")
  void testGetGavetaByIdComSucesso() {
    Gaveta gaveta = mock(Gaveta.class);
    GavetaResponseDTO dto = new GavetaResponseDTO();
    dto.setId(1L);

    when(gavetaRepository.findById(1L)).thenReturn(Optional.of(gaveta));
    when(gavetaMapper.toDto(gaveta)).thenReturn(dto);

    GavetaResponseDTO resultado = gavetaService.getGavetaById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por ID inexistente")
  void testGetGavetaByIdLancaExcecaoQuandoNaoEncontrado() {
    when(gavetaRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      gavetaService.getGavetaById(999L);
    });

    assertTrue(exception.getMessage().contains("Gaveta não econtrada"));
  }

  @Test
  @DisplayName("Deve criar gaveta com sucesso")
  void testCreateGavetaComSucesso() {
    Gaveta gavetaEntity = mock(Gaveta.class);
    GavetaResponseDTO dto = mock(GavetaResponseDTO.class);

    when(jazigoService.getJazigoEntityById(1L)).thenReturn(jazigoEntity);
    when(pessoaService.getPessoEntityById(1L)).thenReturn(pessoaEntity);
    when(gavetaMapper.toEntity(gavetaCreateDTO)).thenReturn(gavetaEntity);
    when(gavetaRepository.save(gavetaEntity)).thenReturn(gavetaEntity);
    when(gavetaMapper.toDto(gavetaEntity)).thenReturn(dto);

    GavetaResponseDTO resultado = gavetaService.createGaveta(gavetaCreateDTO);

    assertNotNull(resultado);
    verify(jazigoService, times(1)).getJazigoEntityById(1L);
    verify(pessoaService, times(1)).getPessoEntityById(1L);
    verify(gavetaRepository, times(1)).save(gavetaEntity);
  }

  @Test
  @DisplayName("Deve atualizar gaveta com sucesso")
  void testUpdateGavetaComSucesso() {
    Gaveta gaveta = mock(Gaveta.class);
    GavetaUpdateDTO updateDTO = new GavetaUpdateDTO();
    updateDTO.setJazigo(2L);
    updateDTO.setOcupante(2L);
    updateDTO.setNumero(2);
    GavetaResponseDTO dto = mock(GavetaResponseDTO.class);

    when(gavetaRepository.findById(1L)).thenReturn(Optional.of(gaveta));
    when(jazigoService.getJazigoEntityById(anyLong())).thenReturn(jazigoEntity);
    when(pessoaService.getPessoEntityById(anyLong())).thenReturn(pessoaEntity);
    when(gavetaRepository.save(gaveta)).thenReturn(gaveta);
    when(gavetaMapper.toDto(gaveta)).thenReturn(dto);

    GavetaResponseDTO resultado = gavetaService.updateGaveta(1L, updateDTO);

    assertNotNull(resultado);
    verify(gavetaRepository, times(1)).save(gaveta);
    verify(jazigoService, times(1)).getJazigoEntityById(2L);
    verify(pessoaService, times(1)).getPessoEntityById(2L);
  }

  @Test
  @DisplayName("Deve deletar gaveta com sucesso")
  void testDeleteGavetaComSucesso() {
    Gaveta gaveta = mock(Gaveta.class);
    when(gavetaRepository.findById(1L)).thenReturn(Optional.of(gaveta));

    gavetaService.deleteGaveta(1L);

    verify(gavetaRepository, times(1)).delete(gaveta);
  }

  @Test
  @DisplayName("Deve lançar exceção 404 ao deletar gaveta inexistente")
  void testDeleteGavetaLancaExcecaoQuandoNaoEncontrado() {
    when(gavetaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> gavetaService.deleteGaveta(999L));
    verify(gavetaRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade gaveta quando buscar por ID existente")
  void testGetGavetaEntityByIdComSucesso() {
    Gaveta gaveta = Gaveta.builder()
        .id(1L)
        .jazigo(jazigoEntity)
        .numero(1)
        .build();

    when(gavetaRepository.findById(1L)).thenReturn(Optional.of(gaveta));

    Gaveta resultado = gavetaService.getGavetaEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals(1, resultado.getNumero());
  }
}
