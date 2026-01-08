package com.sigesi.sigesi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoRepository;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.UsuarioRepository;
import com.sigesi.sigesi.usuarios.enums.Role;

@Component
public class AdminUserInitializer implements CommandLineRunner {

  private final UsuarioRepository usuarioRepository;
  private final EnderecoRepository enderecoRepository;

  @Value("${app.admin.email}")
  private String adminEmail;

  public AdminUserInitializer(UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository) {
    this.usuarioRepository = usuarioRepository;
    this.enderecoRepository = enderecoRepository;
  }

  @Override
  public void run(String... args) {

    if (adminEmail == null || adminEmail.isBlank()) {
      throw new IllegalStateException("ADMIN_EMAIL não definido");
    }

    usuarioRepository.findByEmail(adminEmail)
        .ifPresentOrElse(
            user -> {
              if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                usuarioRepository.save(user);
              }
            },
            () -> {
              Usuario admin = Usuario.builder()
                  .email(adminEmail)
                  .ativo(true)
                  .provider("google")
                  .role(Role.ADMIN)
                  .build();

              usuarioRepository.save(admin);
            });

    if (enderecoRepository.count() == 0) {
      Endereco endereco = Endereco.builder()
          .logradouro("Rua Principal")
          .numero("123")
          .bairro("Centro")
          .referencia("Perto da Praça")
          .build();
      enderecoRepository.save(endereco);
    }
  }
}
