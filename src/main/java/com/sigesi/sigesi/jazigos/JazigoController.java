package com.sigesi.sigesi.jazigos;

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

import com.sigesi.sigesi.jazigos.dtos.JazigoCreateDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoResponseDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoUpdateDTO;

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
  public ResponseEntity<List<JazigoResponseDTO>> listAll() {
    List<JazigoResponseDTO> jazigos = jazigoService.getAll();
    return ResponseEntity.ok(jazigos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<JazigoResponseDTO> getJazigoById(@PathVariable Long id) {
    JazigoResponseDTO jazigo = jazigoService.getJazigoById(id);
    return ResponseEntity.ok(jazigo);
  }

  @PostMapping("/")
  public ResponseEntity<JazigoResponseDTO> createJazigo(@Valid @RequestBody JazigoCreateDTO jazigo) {
    JazigoResponseDTO novoJazigo = jazigoService.createJazigo(jazigo);
    return ResponseEntity.status(HttpStatus.CREATED).body(novoJazigo);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<JazigoResponseDTO> updateJazigo(@PathVariable Long id,
      @Valid @RequestBody JazigoUpdateDTO jazigo) {
    JazigoResponseDTO jazigoAtualizado = jazigoService.updateJazigo(id, jazigo);
    return ResponseEntity.ok(jazigoAtualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteJazigo(@PathVariable Long id) {
    jazigoService.deleteJazigo(id);
    return ResponseEntity.noContent().build();
  }
}
