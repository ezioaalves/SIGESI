package com.sigesi.sigesi.cemiterios;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.cemiterios.dtos.CemiterioCreateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioResponseDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CemiterioMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "endereco", ignore = true)
  Cemiterio toEntity(CemiterioCreateDTO dto);

  CemiterioResponseDTO toDto(Cemiterio entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "endereco", ignore = true)
  void updateFromDto(CemiterioUpdateDTO dto, @MappingTarget Cemiterio entity);
}
