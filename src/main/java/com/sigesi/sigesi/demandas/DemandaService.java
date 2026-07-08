package com.sigesi.sigesi.demandas;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaMaterialCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.materiais.Material;
import com.sigesi.sigesi.materiais.MaterialService;
import com.sigesi.sigesi.notifications.NotificationPublisher;
import com.sigesi.sigesi.solicitacoes.Solicitacao;
import com.sigesi.sigesi.solicitacoes.SolicitacaoService;
import com.sigesi.sigesi.solicitacoes.SolicitacaoStatus;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Service para Demanda.
 */
@Service
public class DemandaService {

  @Autowired
  private DemandaRepository demandaRepository;

  @Autowired
  private DemandaMapper demandaMapper;

  @Autowired
  private SolicitacaoService solicitacaoService;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private MaterialService materialService;

  @Autowired
  private NotificationPublisher notificationPublisher;

  /**
   * Lista todas as demandas.
   */
  public List<DemandaResponseDTO> getAll() {
    return demandaRepository.findAllByOrderByIdAsc()
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca demanda por ID.
   */
  public DemandaResponseDTO getDemandaById(Long id) {
    Demanda demanda = this.getDemandaEntityById(id);
    return demandaMapper.toDto(demanda);
  }

  /**
   * Busca demandas por solicitacao.
   */
  public List<DemandaResponseDTO> getDemandasBySolicitacao(Long solicitacaoId) {
    return demandaRepository.findBySolicitacaoIdOrderByPrazoAsc(solicitacaoId)
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca demandas por responsavel.
   */
  public List<DemandaResponseDTO> getDemandasByResponsavel(Long responsavelId) {
    return demandaRepository.findByResponsavelIdOrderByPrazoAsc(responsavelId)
        .stream()
        .map(demandaMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Cria nova demanda.
   */
  @Transactional
  public DemandaResponseDTO createDemanda(DemandaCreateDTO dto) {
    Solicitacao solicitacao = solicitacaoService
        .getSolicitacaoEntityById(dto.getSolicitacaoId());
    prepareSolicitacaoForDemanda(solicitacao);

    Demanda demanda = demandaMapper.toEntity(dto);
    demanda.setSolicitacao(solicitacao);

    if (dto.getResponsavelId() != null) {
      Usuario responsavel = usuarioService.getUsuarioById(dto.getResponsavelId());
      demanda.setResponsavel(responsavel);
    }

    resolveMateriais(dto.getMateriais(), demanda);

    Demanda saved = demandaRepository.save(demanda);

    // Publish notification event if demand was assigned to a user
    if (saved.getResponsavel() != null) {
      notificationPublisher.publishDemandAssigned(saved);
    }

    return demandaMapper.toDto(saved);
  }

  private void prepareSolicitacaoForDemanda(Solicitacao solicitacao) {
    SolicitacaoStatus status = solicitacao.getStatus();
    if (status == SolicitacaoStatus.ABERTA) {
      solicitacao.setStatus(SolicitacaoStatus.EM_ANDAMENTO);
    } else if (status != SolicitacaoStatus.EM_ANDAMENTO) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Só é possível criar demanda para solicitação aberta ou em andamento");
    }
  }

  /**
   * Atualiza demanda existente.
   */
  @Transactional
  public DemandaResponseDTO updateDemanda(Long id, DemandaUpdateDTO dto) {
    Demanda demanda = this.getDemandaEntityById(id);

    // Store old status for notification
    DemandaStatus oldStatus = demanda.getStatus();

    demandaMapper.updateFromDto(dto, demanda);

    if (dto.getResponsavelId() != null) {
      Usuario responsavel = usuarioService.getUsuarioById(dto.getResponsavelId());
      demanda.setResponsavel(responsavel);
    }

    if (dto.getMateriais() != null) {
      demanda.getMateriais().clear();
      resolveMateriais(dto.getMateriais(), demanda);
    }

    Demanda updated = demandaRepository.save(demanda);

    // Publish notification event if status changed
    if (oldStatus != updated.getStatus() && updated.getResponsavel() != null) {
      notificationPublisher.publishDemandStatusChanged(updated, oldStatus);
    }

    // Publish notification event if demand was assigned to a new user
    if (dto.getResponsavelId() != null && updated.getResponsavel() != null) {
      notificationPublisher.publishDemandAssigned(updated);
    }

    return demandaMapper.toDto(updated);
  }

  /**
   * Deleta demanda.
   */
  public void deleteDemanda(Long id) {
    Demanda demanda = this.getDemandaEntityById(id);
    demandaRepository.delete(demanda);
  }

  /**
   * Busca entidade Demanda por ID (uso interno).
   */
  public Demanda getDemandaEntityById(Long id) {
    return demandaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Demanda não encontrada com id " + id));
  }

  /**
   * Resolve materiais do DTO e adiciona a demanda.
   */
  private void resolveMateriais(List<DemandaMaterialCreateDTO> items, Demanda demanda) {
    if (items == null || items.isEmpty()) {
      return;
    }
    Set<Long> materialIds = new HashSet<>();
    for (DemandaMaterialCreateDTO item : items) {
      if (!materialIds.add(item.getMaterialId())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Material duplicado na demanda: " + item.getMaterialId());
      }
      Material material = materialService
          .getMaterialEntityById(item.getMaterialId());
      DemandaMaterial dm = DemandaMaterial.builder()
          .material(material)
          .quantidade(item.getQuantidade())
          .build();
      demanda.addDemandaMaterial(dm);
    }
  }
}
