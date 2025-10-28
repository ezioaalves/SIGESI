package com.sigesi.sigesi.jazigos;

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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de jazigos.
 */
@RestController
@RequestMapping("/api/jazigos")
@Tag(name = "jazigos")
public class JazigoController {

  @Autowired
  private JazigoService jazigoService;

  @GetMapping("/")
  public ResponseEntity<List<Jazigo>> listAll() {
    List<Jazigo> jazigos = jazigoService.getAll();
    return ResponseEntity.ok(jazigos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Jazigo> getJazigoById(@PathVariable Long id) {
    Jazigo jazigo = jazigoService.getJazigoById(id);
    return ResponseEntity.ok(jazigo);
  }

  @PostMapping("/")
  public ResponseEntity<Jazigo> createJazigo(@Valid @RequestBody Jazigo jazigo) {
    Jazigo novoJazigo = jazigoService.createJazigo(jazigo);
    return ResponseEntity.status(HttpStatus.CREATED).body(novoJazigo);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Jazigo> updateJazigo(@PathVariable Long id,
      @Valid @RequestBody Jazigo jazigo) {
    Jazigo jazigoAtualizado = jazigoService.updateJazigo(id, jazigo);
    return ResponseEntity.ok(jazigoAtualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteJazigo(@PathVariable Long id) {
    jazigoService.deleteJazigo(id);
    return ResponseEntity.noContent().build();
  }
}
