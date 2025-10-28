package com.sigesi.sigesi.gavetas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de gavetas.
 */
@RestController
@RequestMapping("/api/gavetas")
public class GavetaController {

  @Autowired
  private GavetaService gavetaService;

  @GetMapping("/")
  public ResponseEntity<List<Gaveta>> listAll() {
    List<Gaveta> gavetas = gavetaService.getAll();
    return ResponseEntity.ok(gavetas);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Gaveta> getGavetaById(@PathVariable Long id) {
    Gaveta gaveta = gavetaService.getGavetaById(id);
    return ResponseEntity.ok(gaveta);
  }

  @PostMapping("/")
  public ResponseEntity<Gaveta> createGaveta(@Valid @RequestBody Gaveta gaveta) {
    Gaveta novaGaveta = gavetaService.createGaveta(gaveta);
    return ResponseEntity.status(HttpStatus.CREATED).body(novaGaveta);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Gaveta> updateGaveta(@PathVariable Long id,
      @Valid @RequestBody Gaveta gaveta) {
    Gaveta gavetaAtualizada = gavetaService.updateGaveta(id, gaveta);
    return ResponseEntity.ok(gavetaAtualizada);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGaveta(@PathVariable Long id) {
    gavetaService.deleteGaveta(id);
    return ResponseEntity.noContent().build();
  }
}
