package com.sigesi.sigesi.auditoria;

import lombok.Getter;

@Getter
public enum AuditableEntity {
  ARQUIVO("com.sigesi.sigesi.arquivos.Arquivo"),
  ENDERECO("com.sigesi.sigesi.enderecos.Endereco"),
  MATERIAL("com.sigesi.sigesi.materiais.Material"),
  SOLICITACAO("com.sigesi.sigesi.solicitacoes.Solicitacao"),
  USUARIO("com.sigesi.sigesi.usuarios.Usuario"),
  DEMANDA("com.sigesi.sigesi.demandas.Demanda");

  private final String fullPath;

  AuditableEntity(String fullPath) {
    this.fullPath = fullPath;
  }
}
