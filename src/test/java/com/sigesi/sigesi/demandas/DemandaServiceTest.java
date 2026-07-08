package com.sigesi.sigesi.demandas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaMaterialCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.materiais.MaterialService;
import com.sigesi.sigesi.notifications.NotificationPublisher;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.SexoEnum;
import com.sigesi.sigesi.solicitacoes.Solicitacao;
import com.sigesi.sigesi.solicitacoes.SolicitacaoAssunto;
import com.sigesi.sigesi.solicitacoes.SolicitacaoStatus;
import com.sigesi.sigesi.solicitacoes.SolicitacaoService;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Testes unitarios para DemandaService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DemandaService Tests")
class DemandaServiceTest {

  @Mock
  private DemandaRepository demandaRepository;

  @Mock
  private DemandaMapper demandaMapper;

  @Mock
  private SolicitacaoService solicitacaoService;

  @Mock
  private UsuarioService usuarioService;

  @Mock
  private MaterialService materialService;

  @Mock
  private NotificationPublisher notificationPublisher;

  @InjectMocks
  private DemandaService demandaService;

  private Solicitacao solicitacao;
  private Usuario responsavel;
  private Demanda demanda;
  private DemandaResponseDTO responseDTO;

  @BeforeEach
  void setUp() {
    solicitacao = Solicitacao.builder()
        .id(1L).body("Descricao").assunto(SolicitacaoAssunto.BURACO)
        .status(SolicitacaoStatus.ABERTA)
        .build();
    responsavel = Usuario.builder()
        .id(2L).email("agent@test.com").name("Agente")
        .build();
    demanda = Demanda.builder()
        .id(1L).solicitacao(solicitacao).responsavel(responsavel)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.PENDENTE)
        .build();
    responseDTO = mock(DemandaResponseDTO.class);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando nao ha demandas")
  void testGetAllRetornaListaVazia() {
    when(demandaRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<DemandaResponseDTO> resultado = demandaService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
  }

  @Test
  @DisplayName("Deve retornar lista de demandas")
  void testGetAllRetornaLista() {
    Demanda d1 = mock(Demanda.class);
    Demanda d2 = mock(Demanda.class);

    when(demandaRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(d1, d2));
    when(demandaMapper.toDto(any())).thenReturn(mock(DemandaResponseDTO.class));

    List<DemandaResponseDTO> resultado = demandaService.getAll();

    assertEquals(2, resultado.size());
  }

  @Test
  @DisplayName("Deve retornar demanda por ID")
  void testGetDemandaByIdComSucesso() {
    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(demandaMapper.toDto(demanda)).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.getDemandaById(1L);

    assertNotNull(resultado);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException quando ID nao encontrado")
  void testGetDemandaByIdNaoEncontrado() {
    when(demandaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> demandaService.getDemandaById(999L));
  }

  @Test
  @DisplayName("Deve retornar demandas por solicitacao")
  void testGetDemandasBySolicitacao() {
    when(demandaRepository.findBySolicitacaoIdOrderByPrazoAsc(1L))
        .thenReturn(List.of(demanda));
    when(demandaMapper.toDto(any())).thenReturn(mock(DemandaResponseDTO.class));

    List<DemandaResponseDTO> resultado = demandaService.getDemandasBySolicitacao(1L);

    assertEquals(1, resultado.size());
  }

  @Test
  @DisplayName("Deve retornar demandas por responsavel")
  void testGetDemandasByResponsavel() {
    when(demandaRepository.findByResponsavelIdOrderByPrazoAsc(2L))
        .thenReturn(List.of(demanda));
    when(demandaMapper.toDto(any())).thenReturn(mock(DemandaResponseDTO.class));

    List<DemandaResponseDTO> resultado = demandaService.getDemandasByResponsavel(2L);

    assertEquals(1, resultado.size());
  }

  @Test
  @DisplayName("Deve criar demanda com sucesso")
  void testCreateDemandaComSucesso() {
    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    Demanda novaDemanda = Demanda.builder()
        .id(1L).solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(novaDemanda);
    when(demandaRepository.save(any())).thenReturn(novaDemanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.createDemanda(createDTO);

    assertNotNull(resultado);
    assertEquals(SolicitacaoStatus.EM_ANDAMENTO, solicitacao.getStatus());
    verify(demandaRepository, times(1)).save(any());
    verify(notificationPublisher, never()).publishDemandAssigned(any());
  }

  @Test
  @DisplayName("Deve criar demanda para solicitacao com solicitante pessoa")
  void testCreateDemandaComSolicitantePessoa() {
    Pessoa solicitante = Pessoa.builder()
        .id(4L)
        .nome("Cidadao Teste")
        .cpf("12345678900")
        .sexo(SexoEnum.MASCULINO)
        .build();
    solicitacao.setSolicitante(solicitante);
    solicitacao.setStatus(SolicitacaoStatus.ABERTA);

    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    Demanda novaDemanda = Demanda.builder()
        .id(1L)
        .solicitacao(solicitacao)
        .prazo(createDTO.getPrazo())
        .status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(novaDemanda);
    when(demandaRepository.save(any())).thenReturn(novaDemanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.createDemanda(createDTO);

    assertNotNull(resultado);
    assertEquals(SolicitacaoStatus.EM_ANDAMENTO, solicitacao.getStatus());
    verify(demandaRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Deve criar demanda para solicitacao ja em andamento")
  void testCreateDemandaComSolicitacaoEmAndamento() {
    solicitacao.setStatus(SolicitacaoStatus.EM_ANDAMENTO);

    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    Demanda novaDemanda = Demanda.builder()
        .id(1L)
        .solicitacao(solicitacao)
        .prazo(createDTO.getPrazo())
        .status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(novaDemanda);
    when(demandaRepository.save(any())).thenReturn(novaDemanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    demandaService.createDemanda(createDTO);

    assertEquals(SolicitacaoStatus.EM_ANDAMENTO, solicitacao.getStatus());
    verify(demandaRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Deve rejeitar demanda para solicitacao em estado final")
  void testCreateDemandaRejeitaSolicitacaoFinalizada() {
    solicitacao.setStatus(SolicitacaoStatus.CONCLUIDA);

    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);

    assertThrows(ResponseStatusException.class,
        () -> demandaService.createDemanda(createDTO));
    verify(demandaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve criar demanda com responsavel e publicar notificacao")
  void testCreateDemandaComResponsavel() {
    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setResponsavelId(2L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(usuarioService.getUsuarioById(2L)).thenReturn(responsavel);
    when(demandaMapper.toEntity(createDTO)).thenReturn(demanda);
    when(demandaRepository.save(any())).thenReturn(demanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.createDemanda(createDTO);

    assertNotNull(resultado);
    verify(notificationPublisher, times(1)).publishDemandAssigned(demanda);
  }

  @Test
  @DisplayName("Deve criar demanda sem responsavel sem publicar notificacao")
  void testCreateDemandaSemResponsavel() {
    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));

    Demanda semResponsavel = Demanda.builder()
        .id(1L).solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(semResponsavel);
    when(demandaRepository.save(any())).thenReturn(semResponsavel);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    demandaService.createDemanda(createDTO);

    verify(notificationPublisher, never()).publishDemandAssigned(any());
  }

  @Test
  @DisplayName("Deve criar demanda com materiais")
  void testCreateDemandaComMateriais() {
    DemandaMaterialCreateDTO matDto = new DemandaMaterialCreateDTO(10L, 5);
    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));
    createDTO.setMateriais(List.of(matDto));

    Material material = Material.builder().id(10L).nome("Cimento").preco(50.0).build();
    Demanda novaDemanda = Demanda.builder()
        .id(1L).solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(novaDemanda);
    when(materialService.getMaterialEntityById(10L)).thenReturn(material);
    when(demandaRepository.save(any())).thenReturn(novaDemanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.createDemanda(createDTO);

    assertNotNull(resultado);
    verify(materialService, times(1)).getMaterialEntityById(10L);
    assertEquals(1, novaDemanda.getMateriais().size());
    DemandaMaterial demandaMaterial = novaDemanda.getMateriais().iterator().next();
    assertEquals(material, demandaMaterial.getMaterial());
    assertEquals(5, demandaMaterial.getQuantidade());
    assertEquals(novaDemanda, demandaMaterial.getDemanda());
  }

  @Test
  @DisplayName("Deve atualizar demanda com sucesso")
  void testUpdateDemandaComSucesso() {
    DemandaUpdateDTO updateDTO = new DemandaUpdateDTO();
    updateDTO.setPrazo(LocalDate.now().plusDays(14));

    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(demandaRepository.save(any())).thenReturn(demanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.updateDemanda(1L, updateDTO);

    assertNotNull(resultado);
    verify(demandaMapper, times(1)).updateFromDto(updateDTO, demanda);
  }

  @Test
  @DisplayName("Deve publicar notificacao quando status muda")
  void testUpdateDemandaMudaStatus() {
    demanda.setStatus(DemandaStatus.PENDENTE);
    DemandaUpdateDTO updateDTO = new DemandaUpdateDTO();
    updateDTO.setStatus(DemandaStatus.EM_ANDAMENTO);

    Demanda updated = Demanda.builder()
        .id(1L).solicitacao(solicitacao).responsavel(responsavel)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.EM_ANDAMENTO)
        .build();

    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(demandaRepository.save(any())).thenReturn(updated);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    demandaService.updateDemanda(1L, updateDTO);

    verify(notificationPublisher, times(1))
        .publishDemandStatusChanged(updated, DemandaStatus.PENDENTE);
  }

  @Test
  @DisplayName("Deve encerrar solicitacao vinculada quando demanda e concluida")
  void testUpdateDemandaConcluidaEncerraSolicitacao() {
    demanda.setStatus(DemandaStatus.EM_ANDAMENTO);
    solicitacao.setStatus(SolicitacaoStatus.EM_ANDAMENTO);
    DemandaUpdateDTO updateDTO = new DemandaUpdateDTO();
    updateDTO.setStatus(DemandaStatus.CONCLUIDA);

    doAnswer(invocation -> {
      DemandaUpdateDTO dto = invocation.getArgument(0);
      Demanda entity = invocation.getArgument(1);
      entity.setStatus(dto.getStatus());
      return null;
    }).when(demandaMapper).updateFromDto(updateDTO, demanda);
    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(demandaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    DemandaResponseDTO resultado = demandaService.updateDemanda(1L, updateDTO);

    assertNotNull(resultado);
    assertEquals(SolicitacaoStatus.ENCERRADA, solicitacao.getStatus());
  }

  @Test
  @DisplayName("Deve publicar notificacao ao atribuir responsavel")
  void testUpdateDemandaAtribuiResponsavel() {
    DemandaUpdateDTO updateDTO = new DemandaUpdateDTO();
    updateDTO.setResponsavelId(2L);

    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(usuarioService.getUsuarioById(2L)).thenReturn(responsavel);
    when(demandaRepository.save(any())).thenReturn(demanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    demandaService.updateDemanda(1L, updateDTO);

    verify(notificationPublisher, times(1)).publishDemandAssigned(demanda);
  }

  @Test
  @DisplayName("Deve atualizar materiais da demanda")
  void testUpdateDemandaComMateriais() {
    DemandaMaterialCreateDTO matDto = new DemandaMaterialCreateDTO(10L, 3);
    DemandaUpdateDTO updateDTO = new DemandaUpdateDTO();
    updateDTO.setMateriais(List.of(matDto));

    Material material = Material.builder().id(10L).nome("Cimento").preco(50.0).build();

    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));
    when(materialService.getMaterialEntityById(10L)).thenReturn(material);
    when(demandaRepository.save(any())).thenReturn(demanda);
    when(demandaMapper.toDto(any())).thenReturn(responseDTO);

    demandaService.updateDemanda(1L, updateDTO);

    verify(materialService, times(1)).getMaterialEntityById(10L);
    assertEquals(1, demanda.getMateriais().size());
    DemandaMaterial demandaMaterial = demanda.getMateriais().iterator().next();
    assertEquals(material, demandaMaterial.getMaterial());
    assertEquals(3, demandaMaterial.getQuantidade());
  }

  @Test
  @DisplayName("Deve rejeitar materiais duplicados na demanda")
  void testCreateDemandaRejeitaMateriaisDuplicados() {
    DemandaCreateDTO createDTO = new DemandaCreateDTO();
    createDTO.setSolicitacaoId(1L);
    createDTO.setPrazo(LocalDate.now().plusDays(7));
    createDTO.setMateriais(List.of(
        new DemandaMaterialCreateDTO(10L, 3),
        new DemandaMaterialCreateDTO(10L, 4)));

    Demanda novaDemanda = Demanda.builder()
        .id(1L).solicitacao(solicitacao)
        .prazo(LocalDate.now().plusDays(7)).status(DemandaStatus.PENDENTE)
        .build();

    when(solicitacaoService.getSolicitacaoEntityById(1L)).thenReturn(solicitacao);
    when(demandaMapper.toEntity(createDTO)).thenReturn(novaDemanda);

    assertThrows(ResponseStatusException.class,
        () -> demandaService.createDemanda(createDTO));
    verify(demandaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao atualizar inexistente")
  void testUpdateDemandaNaoEncontrado() {
    when(demandaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> demandaService.updateDemanda(999L, new DemandaUpdateDTO()));
  }

  @Test
  @DisplayName("Deve deletar demanda com sucesso")
  void testDeleteDemandaComSucesso() {
    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));

    demandaService.deleteDemanda(1L);

    verify(demandaRepository, times(1)).delete(demanda);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao deletar inexistente")
  void testDeleteDemandaNaoEncontrado() {
    when(demandaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> demandaService.deleteDemanda(999L));
    verify(demandaRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade Demanda por ID")
  void testGetDemandaEntityByIdComSucesso() {
    when(demandaRepository.findById(1L)).thenReturn(Optional.of(demanda));

    Demanda resultado = demandaService.getDemandaEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para entidade inexistente")
  void testGetDemandaEntityByIdNaoEncontrado() {
    when(demandaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> demandaService.getDemandaEntityById(999L));
  }
}
