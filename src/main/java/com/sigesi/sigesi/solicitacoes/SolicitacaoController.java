package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.authentication.CustomOAuth2User;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para Solicitacao.
 */
@RestController
@RequestMapping("/api/solicitacoes")
@Tag(name = "solicitacoes")
public class SolicitacaoController {

  @Autowired
  private SolicitacaoService solicitacaoService;

  @GetMapping("/")
  public ResponseEntity<List<SolicitacaoResponseDTO>> listAll(
      Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    List<SolicitacaoResponseDTO> solicitacoes =
        solicitacaoService.getAll(user.getUser());
    return ResponseEntity.ok(solicitacoes);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SolicitacaoResponseDTO> getSolicitacaoById(@PathVariable Long id) {
    SolicitacaoResponseDTO solicitacao = solicitacaoService.getSolicitacaoById(id);
    return ResponseEntity.ok(solicitacao);
  }

  @PostMapping("/")
  public ResponseEntity<SolicitacaoResponseDTO> createSolicitacao(
      @Valid @RequestBody SolicitacaoCreateDTO dto, Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    SolicitacaoResponseDTO result = solicitacaoService.createSolicitacao(dto, user.getUser());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SolicitacaoResponseDTO> updateSolicitacao(
      @PathVariable Long id, @Valid @RequestBody SolicitacaoUpdateDTO dto) {
    SolicitacaoResponseDTO result = solicitacaoService.updateSolicitacao(id, dto);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSolicitacao(@PathVariable Long id) {
    solicitacaoService.deleteSolicitacao(id);
    return ResponseEntity.noContent().build();
  }
}
