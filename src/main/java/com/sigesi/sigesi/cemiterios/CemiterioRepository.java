package com.sigesi.sigesi.cemiterios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CemiterioRepository extends JpaRepository<Cemiterio, Long> {
}
