package com.sigesi.sigesi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sigesi.sigesi.usuarios.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final UsuarioService usuarioService;
  private final String successRedirect;

  @Autowired
  public OAuth2LoginSuccessHandler(UsuarioService usuarioService, Environment env) {
    this.usuarioService = usuarioService;
    this.successRedirect = env.getProperty(
        "app.oauth2.success-redirect");
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {

    usuarioService.processOAuthPostLogin(
        (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal());

    response.sendRedirect(successRedirect);
  }
}
