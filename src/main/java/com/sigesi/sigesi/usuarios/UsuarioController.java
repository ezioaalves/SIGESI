package com.sigesi.sigesi.usuarios;

import java.util.List;
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

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/me")
  public Object me(Authentication auth) {
    CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
    return Map.of(
        "name", user.getUser().getName(),
        "email", user.getUser().getEmail(),
        "role", user.getUser().getRole(),
        "picture", user.getUser().getPictureUrl());
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

}
