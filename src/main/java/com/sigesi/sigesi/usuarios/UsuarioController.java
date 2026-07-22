package com.sigesi.sigesi.usuarios;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigesi.sigesi.authentication.CustomOAuth2User;
import com.sigesi.sigesi.usuarios.dtos.UsuarioUpdateDTO;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.usuarios.dtos.CadastroCidadaoDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/me")
  public Object me(Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("id", user.getUser().getId());
    response.put("name", user.getUser().getName());
    response.put("email", user.getUser().getEmail());
    response.put("role", user.getUser().getRole());
    response.put("picture", user.getUser().getPictureUrl());
    response.put("pessoa", user.getUser().getPessoa());
    return response;
  }

  @PostMapping("/me/pessoa")
  public ResponseEntity<PessoaResponseDTO> cadastrarPessoaAtual(
      Authentication auth, @Valid @RequestBody CadastroCidadaoDTO dto) {
    CustomOAuth2User principal = (CustomOAuth2User) auth.getPrincipal();
    PessoaResponseDTO pessoa = usuarioService.cadastrarPessoa(principal.getUser(), dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
  }

  @GetMapping("/")
  public ResponseEntity<List<Usuario>> listAll() {
    List<Usuario> usuarios = usuarioService.getAll();
    return ResponseEntity.ok(usuarios);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
    Usuario usuario = usuarioService.getUsuarioById(id);
    return ResponseEntity.ok(usuario);
  }

  @PatchMapping("/{id}/toggle-ativo")
  public ResponseEntity<Usuario> toggleAtivo(@PathVariable Long id) {
    Usuario usuarioAtualizado = usuarioService.toggleUsuarioAtivo(id);
    return ResponseEntity.ok(usuarioAtualizado);
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<Usuario> updateRoleUsuario(@PathVariable Long id,
      @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {

    Usuario usuario = usuarioService.updateUsuario(id, usuarioUpdateDTO);
    return ResponseEntity.ok(usuario);
  }

}
