package com.sigesi.sigesi.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioService;

@Service
public class CustomOidcUserService extends OidcUserService {

  @Autowired
  private UsuarioService usuarioService;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest)
      throws OAuth2AuthenticationException {

    OidcUser oidcUser = super.loadUser(userRequest);

    Usuario user = usuarioService.processOAuthPostLogin(oidcUser);

    if (!user.getAtivo()) {
      throw new OAuth2AuthenticationException(
          "Usuário inativo. Aguardando liberação.");
    }

    return new CustomOAuth2User(oidcUser, user);
  }
}
