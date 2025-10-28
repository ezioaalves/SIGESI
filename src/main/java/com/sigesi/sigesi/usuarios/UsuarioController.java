package com.sigesi.sigesi.usuarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

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
