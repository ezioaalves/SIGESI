package com.sigesi.sigesi.usuarios.enums;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
  CIDADAO,
  OPERADOR,
  AGENTE,
  ADMIN;

  public List<SimpleGrantedAuthority> getAuthorities() {
    return Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + this.name()));
  }
}
