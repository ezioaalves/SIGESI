package com.sigesi.sigesi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.sigesi.sigesi.authentication.CustomOidcUserService;

@Configuration
public class SpringConfig {

  @Autowired
  private OAuth2LoginSuccessHandler successHandler;

  @Autowired
  private CustomOidcUserService customOidcUserService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/enderecos/**").hasAnyAuthority("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
            .requestMatchers("/api/solicitacoes/**").hasAnyAuthority("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
            .requestMatchers("/api/cemiterios/**").hasAnyAuthority("OPERADOR", "ADMIN")
            .requestMatchers("/api/jazigos/**").hasAnyAuthority("OPERADOR", "ADMIN")
            .requestMatchers("/api/gavetas/**").hasAnyAuthority("OPERADOR", "ADMIN")
            .requestMatchers("/api/enderecos/**").hasAnyAuthority("CIDADAO", "AGENTE", "OPERADOR", "ADMIN")
            .requestMatchers("/api/usuarios/me").authenticated()
            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
            .successHandler(successHandler));

    return http.build();
  }
}
