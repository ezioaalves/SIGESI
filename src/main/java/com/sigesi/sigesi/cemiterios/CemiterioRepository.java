package com.sigesi.sigesi.cemiterios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CemiterioRepository extends JpaRepository<Cemiterio, Long> {

  List<Cemiterio> findAllByOrderByIdAsc();
}
