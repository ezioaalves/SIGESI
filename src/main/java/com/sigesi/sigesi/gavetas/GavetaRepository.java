package com.sigesi.sigesi.gavetas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GavetaRepository extends JpaRepository<Gaveta, Long> {
}
