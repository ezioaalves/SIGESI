package com.sigesi.sigesi.solicitacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.springframework.web.server.ResponseStatusException;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.arquivos.ArquivoService;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import com.sigesi.sigesi.pessoas.SexoEnum;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;
import com.sigesi.sigesi.usuarios.enums.Role;

/**
 * Testes unitarios para SolicitacaoService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitacaoService Tests")
class SolicitacaoServiceTest {

  @Mock
  private SolicitacaoRepository solicitacaoRepository;

  @Mock
  private SolicitacaoMapper solicitacaoMapper;

  @Mock
  private UsuarioService usuarioService;

  @Mock
  private PessoaService pessoaService;

  @Mock
  private EnderecoService enderecoService;

  @Mock
  private ArquivoService arquivoService;

  @InjectMocks
  private SolicitacaoService solicitacaoService;

  private Usuario adminUser;
  private Usuario operadorUser;
  private Usuario cidadaoUser;
  private Pessoa cidadaoPessoa;
  private Endereco endereco;
  private Solicitacao solicitacao;
  private SolicitacaoResponseDTO responseDTO;

  @BeforeEach
  void setUp() {
    adminUser = Usuario.builder().id(1L).role(Role.ADMIN).email("admin@test.com").build();
    operadorUser = Usuario.builder().id(2L).role(Role.OPERADOR).email("op@test.com").build();
    cidadaoUser = Usuario.builder().id(3L).role(Role.CIDADAO).email("user@test.com").build();
    cidadaoPessoa = Pessoa.builder()
        .id(4L)
        .nome("Cidadao Teste")
        .cpf("12345678900")
        .sexo(SexoEnum.MASCULINO)
        .build();
    endereco = Endereco.builder()
        .id(1L).logradouro("Rua A").numero("1").bairro("Centro")
        .build();
    solicitacao = Solicitacao.builder()
        .id(1L).body("Descricao").autor(cidadaoUser).solicitante(cidadaoPessoa).local(endereco)
        .status(SolicitacaoStatus.ABERTA)
        .build();
    responseDTO = mock(SolicitacaoResponseDTO.class);
  }

  @Test
  @DisplayName("Deve retornar todas solicitacoes para ADMIN")
  void testGetAllRetornaTodasParaAdmin() {
    Solicitacao s1 = mock(Solicitacao.class);
    Solicitacao s2 = mock(Solicitacao.class);

    when(solicitacaoRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(s1, s2));
    when(solicitacaoMapper.toDto(any())).thenReturn(mock(SolicitacaoResponseDTO.class));

    List<SolicitacaoResponseDTO> resultado = solicitacaoService.getAll(adminUser);

    assertEquals(2, resultado.size());
    verify(solicitacaoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar todas solicitacoes para OPERADOR")
  void testGetAllRetornaTodasParaOperador() {
    when(solicitacaoRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<SolicitacaoResponseDTO> resultado = solicitacaoService.getAll(operadorUser);

    assertNotNull(resultado);
    verify(solicitacaoRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar apenas proprias solicitacoes para CIDADAO")
  void testGetAllRetornaApenasPropriasParaUser() {
    when(solicitacaoRepository.findByAutorIdOrderByDataDesc(3L))
        .thenReturn(List.of(solicitacao));
    when(solicitacaoMapper.toDto(any())).thenReturn(mock(SolicitacaoResponseDTO.class));

    List<SolicitacaoResponseDTO> resultado = solicitacaoService.getAll(cidadaoUser);

    assertEquals(1, resultado.size());
    verify(solicitacaoRepository, times(1)).findByAutorIdOrderByDataDesc(3L);
    verify(solicitacaoRepository, never()).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar solicitacao por ID")
  void testGetSolicitacaoByIdComSucesso() {
    when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
    when(solicitacaoMapper.toDto(solicitacao)).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.getSolicitacaoById(1L);

    assertNotNull(resultado);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException quando ID nao encontrado")
  void testGetSolicitacaoByIdNaoEncontrado() {
    when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> solicitacaoService.getSolicitacaoById(999L));
  }

  @Test
  @DisplayName("Deve criar solicitacao com sucesso")
  void testCreateSolicitacaoComSucesso() {
    SolicitacaoCreateDTO createDTO = new SolicitacaoCreateDTO();
    createDTO.setAssunto(SolicitacaoAssunto.BURACO);
    createDTO.setBody("Descricao do problema");
    createDTO.setSolicitanteId(4L);
    createDTO.setLocalId(1L);

    when(pessoaService.getPessoaEntityById(4L)).thenReturn(cidadaoPessoa);
    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(endereco);
    when(solicitacaoMapper.toEntity(createDTO)).thenReturn(solicitacao);
    when(solicitacaoRepository.save(any())).thenReturn(solicitacao);
    when(solicitacaoMapper.toDto(any())).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.createSolicitacao(createDTO, cidadaoUser);

    assertNotNull(resultado);
    verify(solicitacaoRepository, times(1)).save(any());
    verify(usuarioService, never()).getUsuarioById(any());
  }

  @Test
  @DisplayName("Deve criar solicitacao legada com autor")
  void testCreateSolicitacaoLegadaComAutor() {
    SolicitacaoCreateDTO createDTO = new SolicitacaoCreateDTO();
    createDTO.setAssunto(SolicitacaoAssunto.BURACO);
    createDTO.setBody("Descricao do problema");
    createDTO.setAutorId(3L);
    createDTO.setLocalId(1L);

    when(usuarioService.getUsuarioById(3L)).thenReturn(cidadaoUser);
    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(endereco);
    when(solicitacaoMapper.toEntity(createDTO)).thenReturn(solicitacao);
    when(solicitacaoRepository.save(any())).thenReturn(solicitacao);
    when(solicitacaoMapper.toDto(any())).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.createSolicitacao(createDTO);

    assertNotNull(resultado);
    verify(pessoaService, never()).getPessoaEntityById(any());
    verify(solicitacaoRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Deve rejeitar solicitacao sem autor")
  void testCreateSolicitacaoSemSolicitanteNemAutor() {
    SolicitacaoCreateDTO createDTO = new SolicitacaoCreateDTO();
    createDTO.setAssunto(SolicitacaoAssunto.BURACO);
    createDTO.setBody("Descricao");
    createDTO.setSolicitanteId(4L);
    createDTO.setLocalId(1L);

    assertThrows(ResponseStatusException.class,
        () -> solicitacaoService.createSolicitacao(createDTO));

    verify(solicitacaoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve criar solicitacao com anexos")
  void testCreateSolicitacaoComAnexos() {
    SolicitacaoCreateDTO createDTO = new SolicitacaoCreateDTO();
    createDTO.setAssunto(SolicitacaoAssunto.BURACO);
    createDTO.setBody("Descricao");
    createDTO.setSolicitanteId(4L);
    createDTO.setLocalId(1L);
    createDTO.setAnexoIds(List.of(10L, 20L));

    Arquivo arq1 = Arquivo.builder().id(10L).nomeOriginal("a.pdf").build();
    Arquivo arq2 = Arquivo.builder().id(20L).nomeOriginal("b.pdf").build();

    when(pessoaService.getPessoaEntityById(4L)).thenReturn(cidadaoPessoa);
    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(endereco);
    when(solicitacaoMapper.toEntity(createDTO)).thenReturn(solicitacao);
    when(arquivoService.getArquivoEntityById(10L)).thenReturn(arq1);
    when(arquivoService.getArquivoEntityById(20L)).thenReturn(arq2);
    when(solicitacaoRepository.save(any())).thenReturn(solicitacao);
    when(solicitacaoMapper.toDto(any())).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.createSolicitacao(createDTO, cidadaoUser);

    assertNotNull(resultado);
    verify(arquivoService, times(1)).getArquivoEntityById(10L);
    verify(arquivoService, times(1)).getArquivoEntityById(20L);
  }

  @Test
  @DisplayName("Deve criar solicitacao sem anexos")
  void testCreateSolicitacaoSemAnexos() {
    SolicitacaoCreateDTO createDTO = new SolicitacaoCreateDTO();
    createDTO.setAssunto(SolicitacaoAssunto.ESGOTO);
    createDTO.setBody("Descricao");
    createDTO.setSolicitanteId(4L);
    createDTO.setLocalId(1L);

    when(pessoaService.getPessoaEntityById(4L)).thenReturn(cidadaoPessoa);
    when(enderecoService.getEnderecoEntityById(1L)).thenReturn(endereco);
    when(solicitacaoMapper.toEntity(createDTO)).thenReturn(solicitacao);
    when(solicitacaoRepository.save(any())).thenReturn(solicitacao);
    when(solicitacaoMapper.toDto(any())).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.createSolicitacao(createDTO, cidadaoUser);

    assertNotNull(resultado);
    verify(arquivoService, never()).getArquivoEntityById(any());
  }

  @Test
  @DisplayName("Deve atualizar solicitacao com sucesso")
  void testUpdateSolicitacaoComSucesso() {
    SolicitacaoUpdateDTO updateDTO = new SolicitacaoUpdateDTO(SolicitacaoStatus.EM_ANDAMENTO);

    when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
    when(solicitacaoRepository.save(solicitacao)).thenReturn(solicitacao);
    when(solicitacaoMapper.toDto(solicitacao)).thenReturn(responseDTO);

    SolicitacaoResponseDTO resultado = solicitacaoService.updateSolicitacao(1L, updateDTO);

    assertNotNull(resultado);
    verify(solicitacaoMapper, times(1)).updateFromDto(updateDTO, solicitacao);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao atualizar inexistente")
  void testUpdateSolicitacaoNaoEncontrado() {
    when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> solicitacaoService.updateSolicitacao(999L, new SolicitacaoUpdateDTO()));
  }

  @Test
  @DisplayName("Deve deletar solicitacao com sucesso")
  void testDeleteSolicitacaoComSucesso() {
    when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

    solicitacaoService.deleteSolicitacao(1L);

    verify(solicitacaoRepository, times(1)).delete(solicitacao);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao deletar inexistente")
  void testDeleteSolicitacaoNaoEncontrado() {
    when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> solicitacaoService.deleteSolicitacao(999L));
    verify(solicitacaoRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade Solicitacao por ID")
  void testGetSolicitacaoEntityByIdComSucesso() {
    when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

    Solicitacao resultado = solicitacaoService.getSolicitacaoEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para entidade inexistente")
  void testGetSolicitacaoEntityByIdNaoEncontrado() {
    when(solicitacaoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> solicitacaoService.getSolicitacaoEntityById(999L));
  }
}
