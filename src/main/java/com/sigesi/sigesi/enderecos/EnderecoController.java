package com.sigesi.sigesi.enderecos;

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

/**
 * Controller REST para gerenciamento de endereços.
 */
@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

  @Autowired
  private EnderecoService enderecoService;

  @GetMapping("/")
  public ResponseEntity<List<Endereco>> listAll() {
    List<Endereco> enderecos = enderecoService.getAll();
    return ResponseEntity.ok(enderecos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Endereco> getEnderecoById(@PathVariable Long id) {
    Endereco endereco = enderecoService.getEnderecoById(id);
    return ResponseEntity.ok(endereco);
  }

  @PostMapping("/")
  public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco) {
    Endereco novoEndereco = enderecoService.createEndereco(endereco);
    return ResponseEntity.status(HttpStatus.CREATED).body(novoEndereco);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Endereco> updateEndereco(@PathVariable Long id,
      @RequestBody Endereco endereco) {
    Endereco enderecoAtualizado = enderecoService.updateEndereco(id, endereco);
    return ResponseEntity.ok(enderecoAtualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
    enderecoService.deleteEndereco(id);
    return ResponseEntity.noContent().build();
  }
}
