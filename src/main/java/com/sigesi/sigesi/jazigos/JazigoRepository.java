package com.sigesi.sigesi.jazigos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JazigoRepository extends JpaRepository<Jazigo, Long> {

  List<Jazigo> findAllByOrderByIdAsc();

}
