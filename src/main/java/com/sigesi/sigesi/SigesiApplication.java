package com.sigesi.sigesi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableWebSecurity
public class SigesiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SigesiApplication.class, args);
  }

  @GetMapping("/")
  public String home() {
    return "Bem-vindo à API Spring Boot!";
  }
}
