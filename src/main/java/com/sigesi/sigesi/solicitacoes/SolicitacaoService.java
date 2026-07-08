package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.arquivos.ArquivoService;
import com.sigesi.sigesi.config.ConflictException;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;
import com.sigesi.sigesi.usuarios.enums.Role;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
  private PessoaService pessoaService;

  @Autowired
  private EnderecoService enderecoService;

  @Autowired
  private ArquivoService arquivoService;

  public List<SolicitacaoResponseDTO> getAll(Usuario usuario) {
    List<Solicitacao> solicitacoes;

    if (usuario.getRole() == Role.ADMIN
        || usuario.getRole() == Role.OPERADOR) {
      solicitacoes = solicitacaoRepository.findAllByOrderByIdAsc();
    } else {
      solicitacoes = solicitacaoRepository
          .findByAutorIdOrderByDataDesc(usuario.getId());
    }

    return solicitacoes.stream()
        .map(solicitacaoMapper::toDto)
        .collect(Collectors.toList());
  }

  public SolicitacaoResponseDTO getSolicitacaoById(Long id) {
    Solicitacao solicitacao = this.getSolicitacaoEntityById(id);
    return solicitacaoMapper.toDto(solicitacao);
  }

  public SolicitacaoResponseDTO createSolicitacao(SolicitacaoCreateDTO dto) {
    return createSolicitacao(dto, null);
  }

  public SolicitacaoResponseDTO createSolicitacao(SolicitacaoCreateDTO dto, Usuario usuarioAutenticado) {
    if (dto.getAutorId() == null && usuarioAutenticado == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Autor é obrigatório");
    }

    Pessoa solicitante = null;
    if (dto.getSolicitanteId() != null) {
      solicitante = pessoaService.getPessoaEntityById(dto.getSolicitanteId());
    }

    Usuario autor = usuarioAutenticado;
    if (autor == null && dto.getAutorId() != null) {
      autor = usuarioService.getUsuarioById(dto.getAutorId());
    }

    if (solicitante == null && autor != null) {
      solicitante = resolveSolicitanteForAutor(dto, autor);
    }

    Endereco local = enderecoService.getEnderecoEntityById(dto.getLocalId());

    Solicitacao entity = solicitacaoMapper.toEntity(dto);
    entity.setAutor(autor);
    entity.setSolicitante(solicitante);
    entity.setLocal(local);
    entity.setStatus(SolicitacaoStatus.ABERTA);

    if (dto.getAnexoIds() != null && !dto.getAnexoIds().isEmpty()) {
      List<com.sigesi.sigesi.arquivos.Arquivo> anexos = dto.getAnexoIds().stream()
          .map(id -> arquivoService.getArquivoEntityById(id))
          .collect(Collectors.toList());
      entity.setAnexos(anexos);
    }

    Solicitacao saved = solicitacaoRepository.save(entity);
    return solicitacaoMapper.toDto(saved);
  }

  private Pessoa resolveSolicitanteForAutor(SolicitacaoCreateDTO dto, Usuario autor) {
    if (autor.getPessoa() != null) {
      return autor.getPessoa();
    }

    if (dto.getSolicitante() == null) {
      return null;
    }

    Pessoa pessoa = pessoaService.getOrCreatePessoaEntityByCpf(dto.getSolicitante());
    if (pessoa.getId() != null) {
      usuarioService.findByPessoaId(pessoa.getId())
          .filter(usuario -> !usuario.getId().equals(autor.getId()))
          .ifPresent(usuario -> {
            throw new ConflictException("CPF já vinculado a outro usuário");
          });
    }

    usuarioService.vincularPessoa(autor, pessoa);
    return pessoa;
  }

  public SolicitacaoResponseDTO updateSolicitacao(Long id, SolicitacaoUpdateDTO dto) {
    Solicitacao solicitacao = this.getSolicitacaoEntityById(id);

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
