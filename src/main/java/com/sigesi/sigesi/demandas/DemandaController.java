package com.sigesi.sigesi.demandas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Demanda.
 */
@RestController
@RequestMapping("/api/demandas")
@Tag(name = "demandas")
public class DemandaController {

  @Autowired
  private DemandaService demandaService;

  /**
   * Lista todas as demandas.
   */
  @GetMapping("/")
  public ResponseEntity<List<DemandaResponseDTO>> listAll() {
    List<DemandaResponseDTO> demandas = demandaService.getAll();
    return ResponseEntity.ok(demandas);
  }

  /**
   * Busca demanda por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<DemandaResponseDTO> getDemandaById(@PathVariable Long id) {
    DemandaResponseDTO demanda = demandaService.getDemandaById(id);
    return ResponseEntity.ok(demanda);
  }

  /**
   * Busca demandas por solicitacao.
   */
  @GetMapping("/solicitacao/{solicitacaoId}")
  public ResponseEntity<List<DemandaResponseDTO>> getDemandasBySolicitacao(
      @PathVariable Long solicitacaoId) {
    List<DemandaResponseDTO> demandas = demandaService
        .getDemandasBySolicitacao(solicitacaoId);
    return ResponseEntity.ok(demandas);
  }

  /**
   * Busca demandas por responsavel.
   */
  @GetMapping("/responsavel")
  public ResponseEntity<List<DemandaResponseDTO>> getDemandasByResponsavel(
      @RequestParam Long responsavelId) {
    List<DemandaResponseDTO> demandas = demandaService
        .getDemandasByResponsavel(responsavelId);
    return ResponseEntity.ok(demandas);
  }

  /**
   * Cria nova demanda.
   */
  @PostMapping("/")
  public ResponseEntity<DemandaResponseDTO> createDemanda(
      @Valid @RequestBody DemandaCreateDTO dto) {
    DemandaResponseDTO nova = demandaService.createDemanda(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(nova);
  }

  /**
   * Atualiza demanda existente.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<DemandaResponseDTO> updateDemanda(
      @PathVariable Long id,
      @Valid @RequestBody DemandaUpdateDTO dto) {
    DemandaResponseDTO atualizada = demandaService.updateDemanda(id, dto);
    return ResponseEntity.ok(atualizada);
  }

  /**
   * Deleta demanda.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDemanda(@PathVariable Long id) {
    demandaService.deleteDemanda(id);
    return ResponseEntity.noContent().build();
  }
}
