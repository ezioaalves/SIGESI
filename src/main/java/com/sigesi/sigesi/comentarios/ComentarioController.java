package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Comentario.
 */
@RestController
@RequestMapping("/api/comentarios")
@Tag(name = "comentarios")
public class ComentarioController {

  @Autowired
  private ComentarioService comentarioService;

  /**
   * Lista todos os comentarios.
   */
  @GetMapping("/")
  public ResponseEntity<List<ComentarioResponseDTO>> listAll() {
    List<ComentarioResponseDTO> comentarios = comentarioService.getAll();
    return ResponseEntity.ok(comentarios);
  }

  /**
   * Busca comentario por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ComentarioResponseDTO> getComentarioById(@PathVariable Long id) {
    ComentarioResponseDTO comentario = comentarioService.getComentarioById(id);
    return ResponseEntity.ok(comentario);
  }

  /**
   * Busca comentarios por demanda.
   */
  @GetMapping("/demanda/{demandaId}")
  public ResponseEntity<List<ComentarioResponseDTO>> getComentariosByDemanda(
      @PathVariable Long demandaId) {
    List<ComentarioResponseDTO> comentarios = comentarioService
        .getComentariosByDemanda(demandaId);
    return ResponseEntity.ok(comentarios);
  }

  /**
   * Cria novo comentario.
   */
  @PostMapping("/")
  public ResponseEntity<ComentarioResponseDTO> createComentario(
      @Valid @RequestBody ComentarioCreateDTO dto) {
    ComentarioResponseDTO novo = comentarioService.createComentario(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  /**
   * Deleta comentario.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComentario(@PathVariable Long id) {
    comentarioService.deleteComentario(id);
    return ResponseEntity.noContent().build();
  }
}
