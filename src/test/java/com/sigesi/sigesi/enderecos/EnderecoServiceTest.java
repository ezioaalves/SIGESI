package com.sigesi.sigesi.enderecos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoUpdateDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnderecoService Tests")
class EnderecoServiceTest {

  @Mock
  private EnderecoRepository enderecoRepository;

  @Mock
  private EnderecoMapper enderecoMapper;

  @InjectMocks
  private EnderecoService enderecoService;

  private EnderecoCreateDTO enderecoCreateDTO;
  private EnderecoResponseDTO enderecoDTO;

  @BeforeEach
  void setUp() {
    enderecoCreateDTO = new EnderecoCreateDTO("Rua Exemplo", "123", "Centro", null);
    enderecoDTO = new EnderecoResponseDTO(1L, "Rua Exemplo", "123", "Centro", null);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há endereços")
  void testGetAllRetornaListaVazia() {
    when(enderecoRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<EnderecoResponseDTO> resultado = enderecoService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(enderecoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com endereços existentes")
  void testGetAllRetornaListaComEnderecos() {
    Endereco e1 = mock(Endereco.class);
    Endereco e2 = mock(Endereco.class);
    Endereco e3 = mock(Endereco.class);

    when(enderecoRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(e1, e2, e3));
    when(enderecoMapper.toDto(any())).thenReturn(mock(EnderecoResponseDTO.class));

    List<EnderecoResponseDTO> resultado = enderecoService.getAll();

    assertEquals(3, resultado.size());
    verify(enderecoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar endereço quando buscar por ID existente")
  void testGetEnderecoByIdComSucesso() {
    Endereco endereco = mock(Endereco.class);
    EnderecoResponseDTO dto = new EnderecoResponseDTO(1L, "Rua Exemplo", "123", "Centro", null);

    when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
    when(enderecoMapper.toDto(endereco)).thenReturn(dto);

    EnderecoResponseDTO resultado = enderecoService.getEnderecoById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Rua Exemplo", resultado.getLogradouro());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por ID inexistente")
  void testGetEnderecoByIdLancaExcecaoQuandoNaoEncontrado() {
    when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      enderecoService.getEnderecoById(999L);
    });

    assertTrue(exception.getMessage().contains("Endereço não encontrado"));
  }

  @Test
  @DisplayName("Deve criar endereço com sucesso")
  void testCreateEnderecoComSucesso() {
    Endereco enderecoEntity = mock(Endereco.class);
    EnderecoResponseDTO dto = mock(EnderecoResponseDTO.class);

    when(enderecoMapper.toEntity(enderecoCreateDTO)).thenReturn(enderecoEntity);
    when(enderecoRepository.save(enderecoEntity)).thenReturn(enderecoEntity);
    when(enderecoMapper.toDto(enderecoEntity)).thenReturn(dto);

    EnderecoResponseDTO resultado = enderecoService.createEndereco(enderecoCreateDTO);

    assertNotNull(resultado);
    verify(enderecoRepository, times(1)).save(enderecoEntity);
  }

  @Test
  @DisplayName("Deve atualizar endereço com sucesso")
  void testUpdateEnderecoComSucesso() {
    Endereco endereco = mock(Endereco.class);
    EnderecoUpdateDTO updateDTO = new EnderecoUpdateDTO("Rua Nova", "456", "Bairro Novo", "Nova Referência");
    EnderecoResponseDTO dto = mock(EnderecoResponseDTO.class);

    when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
    when(enderecoRepository.save(endereco)).thenReturn(endereco);
    when(enderecoMapper.toDto(endereco)).thenReturn(dto);

    EnderecoResponseDTO resultado = enderecoService.updateEndereco(1L, updateDTO);

    assertNotNull(resultado);
    verify(enderecoMapper, times(1)).updateFromDto(updateDTO, endereco);
    verify(enderecoRepository, times(1)).save(endereco);
  }

  @Test
  @DisplayName("Deve deletar endereço com sucesso")
  void testDeleteEnderecoComSucesso() {
    Endereco endereco = mock(Endereco.class);
    when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

    enderecoService.deleteEndereco(1L);

    verify(enderecoRepository, times(1)).delete(endereco);
  }

  @Test
  @DisplayName("Deve lançar exceção 404 ao deletar endereço inexistente")
  void testDeleteEnderecoLancaExcecaoQuandoNaoEncontrado() {
    when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> enderecoService.deleteEndereco(999L));
    verify(enderecoRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade endereço quando buscar por ID existente")
  void testGetEnderecoEntityByIdComSucesso() {
    Endereco endereco = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

    Endereco resultado = enderecoService.getEnderecoEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Rua Exemplo", resultado.getLogradouro());
  }
}
