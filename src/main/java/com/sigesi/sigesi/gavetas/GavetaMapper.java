package com.sigesi.sigesi.gavetas;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.mapstruct.NullValuePropertyMappingStrategy;
import com.sigesi.sigesi.gavetas.dtos.GavetaCreateDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaResponseDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GavetaMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "jazigo", ignore = true)
  @Mapping(target = "ocupante", ignore = true)
  Gaveta toEntity(GavetaCreateDTO dto);

  @Mapping(target = "jazigo", ignore = true)
  @Mapping(target = "ocupante", ignore = true)
  GavetaResponseDTO toDto(Gaveta entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "jazigo", ignore = true)
  @Mapping(target = "ocupante", ignore = true)
  void updateFromDto(GavetaUpdateDTO dto, @MappingTarget Gaveta entity);
}
