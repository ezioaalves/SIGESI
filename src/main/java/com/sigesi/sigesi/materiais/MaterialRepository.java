package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para Material.
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

  List<Material> findAllByOrderByIdAsc();

  Set<Material> findAllByIdIn(Set<Long> ids);
}
