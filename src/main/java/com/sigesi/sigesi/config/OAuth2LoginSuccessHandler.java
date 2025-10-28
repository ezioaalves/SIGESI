package com.sigesi.sigesi.config;

import com.sigesi.sigesi.usuarios.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UsuarioService usuarioService;

  public OAuth2LoginSuccessHandler(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {

    usuarioService.processOAuthPostLogin(
        (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal());

    response.sendRedirect("/");
  }
}
