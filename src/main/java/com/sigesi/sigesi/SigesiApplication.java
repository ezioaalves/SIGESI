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

    // Only set specific environment variables as system properties
    String[] allowedKeys = {"MY_APP_CONFIG", "MY_APP_SECRET"}; // TODO: Replace with actual required keys
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
