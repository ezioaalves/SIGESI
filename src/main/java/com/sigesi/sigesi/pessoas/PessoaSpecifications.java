package com.sigesi.sigesi.pessoas;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class PessoaSpecifications {

  public static Specification<Pessoa> filter(String nome, String cpf, SexoEnum sexo, Long enderecoId) {
    return (root, query, cb) -> {
      Predicate p = cb.conjunction();

      if (nome != null && !nome.isBlank()) {
        p = cb.and(p, cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
      }
      if (cpf != null && !cpf.isBlank()) {
        p = cb.and(p, cb.equal(root.get("cpf"), cpf));
      }
      if (sexo != null) {
        p = cb.and(p, cb.equal(root.get("sexo"), sexo));
      }
      if (enderecoId != null) {
        p = cb.and(p, cb.equal(root.get("endereco").get("id"), enderecoId));
      }

      return p;
    };
  }
}
