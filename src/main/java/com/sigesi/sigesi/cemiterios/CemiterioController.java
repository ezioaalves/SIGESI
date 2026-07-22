package com.sigesi.sigesi.cemiterios;

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
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.cemiterios.dtos.CemiterioCreateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioResponseDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de cemitérios.
 */
@RestController
@RequestMapping("/api/cemiterios")
@Tag(name = "cemiterios")
public class CemiterioController {

  @Autowired
  private CemiterioService cemiterioService;

  @GetMapping("/")
  public ResponseEntity<List<CemiterioResponseDTO>> listAll() {
    List<CemiterioResponseDTO> cemiterios = cemiterioService.getAll();
    return ResponseEntity.ok(cemiterios);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CemiterioResponseDTO> getCemiterioById(@PathVariable Long id) {
    CemiterioResponseDTO cemiterio = cemiterioService.getCemiterioById(id);
    return ResponseEntity.ok(cemiterio);
  }

  @PostMapping("/")
  public ResponseEntity<CemiterioResponseDTO> createCemiterio(@Valid @RequestBody CemiterioCreateDTO cemiterio) {
    CemiterioResponseDTO novoCemiterio = cemiterioService.createCemiterio(cemiterio);
    return ResponseEntity.status(HttpStatus.CREATED).body(novoCemiterio);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CemiterioResponseDTO> updateCemiterio(@PathVariable Long id,
      @Valid @RequestBody CemiterioUpdateDTO cemiterio) {
    CemiterioResponseDTO cemiterioAtualizado = cemiterioService.updateCemiterio(id, cemiterio);
    return ResponseEntity.ok(cemiterioAtualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCemiterio(@PathVariable Long id) {
    cemiterioService.deleteCemiterio(id);
    return ResponseEntity.noContent().build();
  }
}
