package com.sigesi.sigesi.usuarios;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.usuarios.dtos.UsuarioUpdateDTO;
import com.sigesi.sigesi.usuarios.enums.Role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioMapper usuarioMapper;

  private void validarUsuarioEditavel(Long id) {
    if (id == 1) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Não é permitido realizar essa ação para este usuário");
    }
  }

  public List<Usuario> getAll() {
    return usuarioRepository.findByIdNot(1L);
  }

  public Usuario getUsuarioById(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id " + id));
  }

  public Usuario toggleUsuarioAtivo(Long id) {
    validarUsuarioEditavel(id);

    Usuario usuario = this.getUsuarioById(id);
    usuario.setAtivo(!usuario.getAtivo());
    return usuarioRepository.save(usuario);
  }

  public Usuario updateUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {
    validarUsuarioEditavel(id);

    Usuario usuario = this.getUsuarioById(id);

    usuarioMapper.updateFromDto(usuarioUpdateDTO, usuario);

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
            .role(Role.CIDADAO)
            .build());

    return usuarioRepository.save(user);
  }
}
