package com.sigesi.sigesi.comentarios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.demandas.Demanda;
import com.sigesi.sigesi.demandas.DemandaService;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

/**
 * Service para Comentario.
 */
@Service
public class ComentarioService {

  @Autowired
  private ComentarioRepository comentarioRepository;

  @Autowired
  private ComentarioMapper comentarioMapper;

  @Autowired
  private DemandaService demandaService;

  @Autowired
  private UsuarioService usuarioService;

  /**
   * Lista todos os comentarios.
   */
  public List<ComentarioResponseDTO> getAll() {
    return comentarioRepository.findAllByOrderByIdAsc()
        .stream()
        .map(comentarioMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca comentario por ID.
   */
  public ComentarioResponseDTO getComentarioById(Long id) {
    Comentario comentario = this.getComentarioEntityById(id);
    return comentarioMapper.toDto(comentario);
  }

  /**
   * Busca comentarios por demanda.
   */
  public List<ComentarioResponseDTO> getComentariosByDemanda(Long demandaId) {
    return comentarioRepository.findByDemandaIdOrderByCriadoEmAsc(demandaId)
        .stream()
        .map(comentarioMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Cria novo comentario.
   */
  public ComentarioResponseDTO createComentario(ComentarioCreateDTO dto) {
    Demanda demanda = demandaService.getDemandaEntityById(dto.getDemandaId());
    Usuario autor = usuarioService.getUsuarioById(dto.getAutorId());

    Comentario comentario = comentarioMapper.toEntity(dto);
    comentario.setDemanda(demanda);
    comentario.setAutor(autor);

    Comentario saved = comentarioRepository.save(comentario);
    return comentarioMapper.toDto(saved);
  }

  /**
   * Deleta comentario.
   */
  public void deleteComentario(Long id) {
    Comentario comentario = this.getComentarioEntityById(id);
    comentarioRepository.delete(comentario);
  }

  /**
   * Busca entidade Comentario por ID (uso interno).
   */
  public Comentario getComentarioEntityById(Long id) {
    return comentarioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Comentário não encontrado com id " + id));
  }
}
