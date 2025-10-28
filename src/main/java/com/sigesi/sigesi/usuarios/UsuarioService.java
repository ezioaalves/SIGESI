package com.sigesi.sigesi.usuarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  public List<Usuario> getAll() {
    return usuarioRepository.findAll();
  }

  public Usuario getUsuarioById(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));
  }

  public Usuario toggleUsuarioAtivo(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));

    usuario.setAtivo(!usuario.getAtivo());

    return usuarioRepository.save(usuario);
  }

  public Usuario processOAuthPostLogin(OAuth2User oAuth2User) {
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String picture = oAuth2User.getAttribute("picture");

    Usuario user = usuarioRepository.findByEmail(email)
        .map(u -> {
          u.setName(name);
          u.setPictureUrl(picture);
          return u;
        })
        .orElse(Usuario.builder()
            .email(email)
            .name(name)
            .pictureUrl(picture)
            .provider("google")
            .ativo(true)
            .build());

    return usuarioRepository.save(user);
  }
}
