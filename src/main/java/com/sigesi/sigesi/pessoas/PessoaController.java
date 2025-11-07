package com.sigesi.sigesi.pessoas;

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

import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de pessoas.
 */
@RestController
@RequestMapping("/api/pessoas")
@Tag(name = "pessoas")
public class PessoaController {

  @Autowired
  private PessoaService pessoaService;

  @GetMapping("/")
  public ResponseEntity<List<PessoaResponseDTO>> listAll() {
    List<PessoaResponseDTO> pessoas = pessoaService.getAll();
    return ResponseEntity.ok(pessoas);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> getPessoaById(@PathVariable Long id) {
    PessoaResponseDTO pessoa = pessoaService.getPessoaById(id);
    return ResponseEntity.ok(pessoa);
  }

  @GetMapping("/cpf")
  public ResponseEntity<PessoaResponseDTO> getPessoaByCpf(@RequestParam String cpf) {
    PessoaResponseDTO pessoa = pessoaService.getPessoaByCpf(cpf);
    return ResponseEntity.ok(pessoa);
  }

  @PostMapping("/")
  public ResponseEntity<PessoaResponseDTO> createPessoa(@Valid @RequestBody PessoaCreateDTO pessoa) {
    PessoaResponseDTO novaPessoa = pessoaService.createPessoa(pessoa);
    return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> updatePessoa(@PathVariable Long id,
      @Valid @RequestBody PessoaUpdateDTO pessoa) {
    PessoaResponseDTO pessoaAtualizada = pessoaService.updatePessoa(id, pessoa);
    return ResponseEntity.ok(pessoaAtualizada);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
    pessoaService.deletePessoa(id);
    return ResponseEntity.noContent().build();
  }
}
