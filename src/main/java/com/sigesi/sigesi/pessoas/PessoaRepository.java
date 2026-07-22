package com.sigesi.sigesi.pessoas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long>, JpaSpecificationExecutor<Pessoa> {
  Optional<Pessoa> findByCpf(String cpf);

  boolean existsByCpf(String cpf);

  List<Pessoa> findAllByOrderByIdAsc();
}
