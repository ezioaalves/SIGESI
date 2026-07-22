package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Mapper MapStruct para Material.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaterialMapper {

  @Mapping(target = "id", ignore = true)
  Material toEntity(MaterialCreateDTO dto);

  MaterialResponseDTO toDto(Material entity);

  List<MaterialResponseDTO> toDtoList(List<Material> entities);

  Set<MaterialResponseDTO> toDtoSet(Set<Material> entities);

  @Mapping(target = "id", ignore = true)
  void updateFromDto(MaterialUpdateDTO dto, @MappingTarget Material entity);
}
