package com.sigesi.sigesi.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Data
@Table(name = "revinfo")
@RevisionEntity(UsuarioRevisionListener.class)
public class UsuarioRevisionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @RevisionNumber
  private Long id;

  @RevisionTimestamp
  private Long timestamp;

  @Column(name = "usuario_id")
  private Long usuarioId;

  @Column(name = "usuario_nome", length = 150)
  private String usuarioNome;

  @Column(name = "usuario_email", length = 150)
  private String usuarioEmail;

}
