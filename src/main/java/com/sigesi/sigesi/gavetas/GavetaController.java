package com.sigesi.sigesi.gavetas;

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

import com.sigesi.sigesi.gavetas.dtos.GavetaCreateDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaResponseDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de gavetas.
 */
@RestController
@RequestMapping("/api/gavetas")
@Tag(name = "gavetas")
public class GavetaController {

  @Autowired
  private GavetaService gavetaService;

  @GetMapping("/")
  public ResponseEntity<List<GavetaResponseDTO>> listAll() {
    List<GavetaResponseDTO> gavetas = gavetaService.getAll();
    return ResponseEntity.ok(gavetas);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GavetaResponseDTO> getGavetaById(@PathVariable Long id) {
    GavetaResponseDTO gaveta = gavetaService.getGavetaById(id);
    return ResponseEntity.ok(gaveta);
  }

  @PostMapping("/")
  public ResponseEntity<GavetaResponseDTO> createGaveta(@Valid @RequestBody GavetaCreateDTO gaveta) {
    GavetaResponseDTO novaGaveta = gavetaService.createGaveta(gaveta);
    return ResponseEntity.status(HttpStatus.CREATED).body(novaGaveta);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<GavetaResponseDTO> updateGaveta(@PathVariable Long id,
      @Valid @RequestBody GavetaUpdateDTO gaveta) {
    GavetaResponseDTO gavetaAtualizada = gavetaService.updateGaveta(id, gaveta);
    return ResponseEntity.ok(gavetaAtualizada);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGaveta(@PathVariable Long id) {
    gavetaService.deleteGaveta(id);
    return ResponseEntity.noContent().build();
  }
}
