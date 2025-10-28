package com.sigesi.sigesi.pessoas;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para gerenciamento de pessoas.
 */
@RestController
@RequestMapping("/api/pessoas")
@Tag(name = "pessoa")
public class PessoaController {

  @Autowired
  private PessoaService pessoaService;

  @GetMapping("/")
  public ResponseEntity<List<Pessoa>> listAll() {
    List<Pessoa> pessoas = pessoaService.getAll();
    return ResponseEntity.ok(pessoas);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Pessoa> getPessoaById(@PathVariable Long id) {
    Pessoa pessoa = pessoaService.getPessoaById(id);
    return ResponseEntity.ok(pessoa);
  }

  @GetMapping("/cpf")
  public ResponseEntity<Pessoa> getPessoaByCpf(@RequestParam String cpf) {
    Pessoa pessoa = pessoaService.getPessoaByCpf(cpf);
    return ResponseEntity.ok(pessoa);
  }

  @PostMapping("/")
  public ResponseEntity<Pessoa> createPessoa(@RequestBody Pessoa pessoa) {
    Pessoa novaPessoa = pessoaService.createPessoa(pessoa);
    return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Pessoa> updatePessoa(@PathVariable Long id,
      @RequestBody Pessoa pessoa) {
    Pessoa pessoaAtualizada = pessoaService.updatePessoa(id, pessoa);
    return ResponseEntity.ok(pessoaAtualizada);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
    pessoaService.deletePessoa(id);
    return ResponseEntity.noContent().build();
  }
}
