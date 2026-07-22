package com.sigesi.sigesi.demandas;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.demandas.dtos.DemandaMaterialResponseDTO;
import com.sigesi.sigesi.materiais.MaterialMapper;

/**
 * Mapper MapStruct para DemandaMaterial.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {MaterialMapper.class})
public interface DemandaMaterialMapper {

  DemandaMaterialResponseDTO toDto(DemandaMaterial entity);

  Set<DemandaMaterialResponseDTO> toDtoSet(Set<DemandaMaterial> entities);
}
