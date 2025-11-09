package com.sigesi.sigesi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SigesiApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load();

    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );

    SpringApplication.run(SigesiApplication.class, args);
  }

  @GetMapping("/")
  public String home() {
    return "Bem-vindo à API Spring Boot!";
  }
}
