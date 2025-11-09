package com.sigesi.sigesi.pessoas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

@ExtendWith(MockitoExtension.class)
@DisplayName("PessoaService Tests")
class PessoaServiceTest {

  @Mock
  private PessoaRepository pessoaRepository;

  @Mock
  private EnderecoService enderecoService;

  @Mock
  private PessoaMapper pessoaMapper;

  @InjectMocks
  private PessoaService pessoaService;

  private PessoaCreateDTO pessoaCreateDTO;
  private Endereco enderecoEntity;

  @BeforeEach
  void setUp() {
    enderecoEntity = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    pessoaCreateDTO = new PessoaCreateDTO();
    pessoaCreateDTO.setNome("João Silva");
    pessoaCreateDTO.setCpf("123.456.789-00");
    pessoaCreateDTO.setSexo(SexoEnum.MASCULINO);
    pessoaCreateDTO.setEnderecoId(1L);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando não há pessoas")
  void testGetAllRetornaListaVazia() {
    when(pessoaRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<PessoaResponseDTO> resultado = pessoaService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(pessoaRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista com pessoas existentes")
  void testGetAllRetornaListaComPessoas() {
    Pessoa p1 = mock(Pessoa.class);
    Pessoa p2 = mock(Pessoa.class);
    Pessoa p3 = mock(Pessoa.class);

    when(pessoaRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(p1, p2, p3));
    when(pessoaMapper.toDto(any())).thenReturn(mock(PessoaResponseDTO.class));

    List<PessoaResponseDTO> resultado = pessoaService.getAll();

    assertEquals(3, resultado.size());
    verify(pessoaRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar pessoa quando buscar por ID existente")
  void testGetPessoaByIdComSucesso() {
    Pessoa pessoa = mock(Pessoa.class);
    PessoaResponseDTO dto = new PessoaResponseDTO();
    dto.setId(1L);
    dto.setNome("João Silva");

    when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
    when(pessoaMapper.toDto(pessoa)).thenReturn(dto);

    PessoaResponseDTO resultado = pessoaService.getPessoaById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("João Silva", resultado.getNome());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por ID inexistente")
  void testGetPessoaByIdLancaExcecaoQuandoNaoEncontrado() {
    when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      pessoaService.getPessoaById(999L);
    });

    assertTrue(exception.getMessage().contains("Pessoa não encontrada"));
  }

  @Test
  @DisplayName("Deve retornar pessoa quando buscar por CPF existente")
  void testGetPessoaByCpfComSucesso() {
    Pessoa pessoa = mock(Pessoa.class);
    PessoaResponseDTO dto = new PessoaResponseDTO();
    dto.setCpf("123.456.789-00");

    when(pessoaRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(pessoa));
    when(pessoaMapper.toDto(pessoa)).thenReturn(dto);

    PessoaResponseDTO resultado = pessoaService.getPessoaByCpf("123.456.789-00");

    assertNotNull(resultado);
    assertEquals("123.456.789-00", resultado.getCpf());
  }

  @Test
  @DisplayName("Deve lançar exceção 404 quando buscar por CPF inexistente")
  void testGetPessoaByCpfLancaExcecaoQuandoNaoEncontrado() {
    when(pessoaRepository.findByCpf(anyString())).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      pessoaService.getPessoaByCpf("999.999.999-99");
    });

    assertTrue(exception.getMessage().contains("Pessoa não encontrada"));
  }

  @Test
  @DisplayName("Deve criar pessoa com sucesso")
  void testCreatePessoaComSucesso() {
    Pessoa pessoaEntity = mock(Pessoa.class);
    PessoaResponseDTO dto = mock(PessoaResponseDTO.class);

    when(pessoaRepository.existsByCpf(anyString())).thenReturn(false);
    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(enderecoEntity);
    when(pessoaMapper.toEntity(pessoaCreateDTO)).thenReturn(pessoaEntity);
    when(pessoaRepository.save(pessoaEntity)).thenReturn(pessoaEntity);
    when(pessoaMapper.toDto(pessoaEntity)).thenReturn(dto);

    PessoaResponseDTO resultado = pessoaService.createPessoa(pessoaCreateDTO);

    assertNotNull(resultado);
    verify(pessoaRepository, times(1)).existsByCpf("123.456.789-00");
    verify(enderecoService, times(1)).getEnderecoEntityById(1L);
    verify(pessoaRepository, times(1)).save(pessoaEntity);
  }

  @Test
  @DisplayName("Deve lançar exceção de conflito quando CPF já existe")
  void testCreatePessoaLancaExcecaoQuandoCpfJaExiste() {
    when(pessoaRepository.existsByCpf(anyString())).thenReturn(true);

    ConflictException exception = assertThrows(ConflictException.class, () -> {
      pessoaService.createPessoa(pessoaCreateDTO);
    });

    assertTrue(exception.getMessage().contains("CPF já cadastrado"));
    verify(pessoaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve atualizar pessoa com sucesso")
  void testUpdatePessoaComSucesso() {
    Pessoa pessoa = mock(Pessoa.class);
    PessoaUpdateDTO updateDTO = new PessoaUpdateDTO();
    updateDTO.setNome("Maria Silva");
    updateDTO.setEnderecoId(1L);
    PessoaResponseDTO dto = mock(PessoaResponseDTO.class);

    when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
    when(enderecoService.getEnderecoEntityById(anyLong())).thenReturn(enderecoEntity);
    when(pessoaRepository.save(pessoa)).thenReturn(pessoa);
    when(pessoaMapper.toDto(pessoa)).thenReturn(dto);

    PessoaResponseDTO resultado = pessoaService.updatePessoa(1L, updateDTO);

    assertNotNull(resultado);
    verify(pessoaMapper, times(1)).updateFromDto(updateDTO, pessoa);
    verify(pessoaRepository, times(1)).save(pessoa);
  }

  @Test
  @DisplayName("Deve deletar pessoa com sucesso")
  void testDeletePessoaComSucesso() {
    Pessoa pessoa = mock(Pessoa.class);
    when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

    pessoaService.deletePessoa(1L);

    verify(pessoaRepository, times(1)).delete(pessoa);
  }

  @Test
  @DisplayName("Deve lançar exceção 404 ao deletar pessoa inexistente")
  void testDeletePessoaLancaExcecaoQuandoNaoEncontrado() {
    when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> pessoaService.deletePessoa(999L));
    verify(pessoaRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade pessoa quando buscar por ID existente")
  void testGetPessoaEntityByIdComSucesso() {
    Pessoa pessoa = Pessoa.builder()
        .id(1L)
        .nome("João Silva")
        .cpf("123.456.789-00")
        .sexo(SexoEnum.MASCULINO)
        .build();

    when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

    Pessoa resultado = pessoaService.getPessoEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("João Silva", resultado.getNome());
  }
}
