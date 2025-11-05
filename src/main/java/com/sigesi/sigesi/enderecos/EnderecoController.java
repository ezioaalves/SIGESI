package com.sigesi.sigesi.enderecos;

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

import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoUpdateDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "enderecos")
public class EnderecoController {

  @Autowired
  private EnderecoService enderecoService;

  @GetMapping("/")
  public ResponseEntity<List<EnderecoResponseDTO>> listAll() {
    List<EnderecoResponseDTO> enderecos = enderecoService.getAll();
    return ResponseEntity.ok(enderecos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EnderecoResponseDTO> getEnderecoById(@PathVariable Long id) {
    EnderecoResponseDTO endereco = enderecoService.getEnderecoById(id);
    return ResponseEntity.ok(endereco);
  }

  @PostMapping("/")
  public ResponseEntity<EnderecoResponseDTO> createEndereco(@Valid @RequestBody EnderecoCreateDTO enderecoDTO) {
    EnderecoResponseDTO novoEndereco = enderecoService.createEndereco(enderecoDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(novoEndereco);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<EnderecoResponseDTO> updateEndereco(@PathVariable Long id,
      @Valid @RequestBody EnderecoUpdateDTO enderecoDTO) {

    EnderecoResponseDTO enderecoAtualizado = enderecoService.updateEndereco(id, enderecoDTO);
    return ResponseEntity.ok(enderecoAtualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
    enderecoService.deleteEndereco(id);
    return ResponseEntity.noContent().build();
  }
}
