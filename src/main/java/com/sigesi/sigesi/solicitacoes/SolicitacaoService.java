package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import com.sigesi.sigesi.usuarios.UsuarioService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço para Solicitacao.
 */
@Service
public class SolicitacaoService {

  @Autowired
  private SolicitacaoRepository solicitacaoRepository;

  @Autowired
  private SolicitacaoMapper solicitacaoMapper;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private EnderecoService enderecoService;

  public List<SolicitacaoResponseDTO> getAll() {
    return solicitacaoRepository.findAllByOrderByIdAsc()
        .stream()
        .map(solicitacaoMapper::toDto)
        .collect(Collectors.toList());
  }

  public SolicitacaoResponseDTO getSolicitacaoById(Long id) {
    Solicitacao solicitacao = this.getSolicitacaoEntityById(id);
    return solicitacaoMapper.toDto(solicitacao);
  }

  public SolicitacaoResponseDTO createSolicitacao(SolicitacaoCreateDTO dto) {
    usuarioService.getUsuarioById(dto.getAutorId());
    enderecoService.getEnderecoEntityById(dto.getLocalId());

    Solicitacao entity = solicitacaoMapper.toEntity(dto);
    Solicitacao saved = solicitacaoRepository.save(entity);
    return solicitacaoMapper.toDto(saved);
  }

  public SolicitacaoResponseDTO updateSolicitacao(Long id, SolicitacaoUpdateDTO dto) {
    Solicitacao solicitacao = this.getSolicitacaoEntityById(id);

    usuarioService.getUsuarioById(dto.getAutorId());
    enderecoService.getEnderecoEntityById(dto.getLocalId());

    solicitacaoMapper.updateFromDto(dto, solicitacao);
    Solicitacao updated = solicitacaoRepository.save(solicitacao);
    return solicitacaoMapper.toDto(updated);
  }

  public void deleteSolicitacao(Long id) {
    Solicitacao solicitacao = this.getSolicitacaoEntityById(id);
    solicitacaoRepository.delete(solicitacao);
  }

  public Solicitacao getSolicitacaoEntityById(Long id) {
    return solicitacaoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Solicitação não encontrada com id " + id));
  }
}
