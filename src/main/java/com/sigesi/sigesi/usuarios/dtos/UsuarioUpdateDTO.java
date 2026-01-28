package com.sigesi.sigesi.usuarios.dtos;

import com.sigesi.sigesi.usuarios.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDTO {

  @NotNull(message = "A role de usuário é obrigatório.")
  private Role role;

}
