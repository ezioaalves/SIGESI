package com.sigesi.sigesi.usuarios;

import org.hibernate.envers.Audited;

import com.sigesi.sigesi.usuarios.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Audited
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String name;
  private String pictureUrl;
  private String provider;
  private Boolean ativo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

}
