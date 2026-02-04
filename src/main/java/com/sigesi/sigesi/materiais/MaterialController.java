package com.sigesi.sigesi.materiais;

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

import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para Material.
 */
@RestController
@RequestMapping("/api/materiais")
@Tag(name = "materiais")
public class MaterialController {

  @Autowired
  private MaterialService materialService;

  /**
   * Lista todos os materiais.
   */
  @GetMapping("/")
  public ResponseEntity<List<MaterialResponseDTO>> listAll() {
    List<MaterialResponseDTO> materiais = materialService.getAll();
    return ResponseEntity.ok(materiais);
  }

  /**
   * Busca material por ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<MaterialResponseDTO> getMaterialById(@PathVariable Long id) {
    MaterialResponseDTO material = materialService.getMaterialById(id);
    return ResponseEntity.ok(material);
  }

  /**
   * Cria novo material.
   */
  @PostMapping("/")
  public ResponseEntity<MaterialResponseDTO> createMaterial(
      @Valid @RequestBody MaterialCreateDTO dto) {
    MaterialResponseDTO novo = materialService.createMaterial(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  /**
   * Atualiza material existente.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<MaterialResponseDTO> updateMaterial(
      @PathVariable Long id,
      @Valid @RequestBody MaterialUpdateDTO dto) {
    MaterialResponseDTO atualizado = materialService.updateMaterial(id, dto);
    return ResponseEntity.ok(atualizado);
  }

  /**
   * Deleta material.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
    materialService.deleteMaterial(id);
    return ResponseEntity.noContent().build();
  }
}
