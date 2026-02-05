package com.sigesi.sigesi.config;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sigesi.sigesi.authentication.CustomOAuth2User;

public class UsuarioRevisionListener implements RevisionListener {

  @Override
  public void newRevision(Object revisionEntity) {
    UsuarioRevisionEntity rev = (UsuarioRevisionEntity) revisionEntity;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
      rev.setUsuarioNome("SYSTEM");
      return;
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof CustomOAuth2User customUser) {
      var user = customUser.getUser();

      rev.setUsuarioId(user.getId());
      rev.setUsuarioNome(user.getName());
      rev.setUsuarioEmail(user.getEmail());
    } else {
      rev.setUsuarioNome(auth.getName());
    }
  }
}
