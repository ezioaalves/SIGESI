package com.sigesi.sigesi.jazigos;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.jazigos.dtos.JazigoCreateDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoResponseDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JazigoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cemiterio", ignore = true)
  Jazigo toEntity(JazigoCreateDTO dto);

  JazigoResponseDTO toDto(Jazigo jazigo);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cemiterio", ignore = true)
  void updateFromDto(JazigoUpdateDTO dto, @MappingTarget Jazigo entity);
}
