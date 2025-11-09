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

    String[] allowedKeys = {"GOOGLE_CLIENT_ID", "GOOGLE_CLIENT_SECRET"};
    for (String key : allowedKeys) {
      String value = dotenv.get(key);
      if (value != null) {
        System.setProperty(key, value);
      }
    }

    SpringApplication.run(SigesiApplication.class, args);
  }

  @GetMapping("/")
  public String home() {
    return "Bem-vindo à API Spring Boot!";
  }
}
