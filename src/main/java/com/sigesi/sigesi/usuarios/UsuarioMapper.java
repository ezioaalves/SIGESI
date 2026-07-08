package com.sigesi.sigesi.usuarios;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.usuarios.dtos.UsuarioUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "pictureUrl", ignore = true)
  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "ativo", ignore = true)
  @Mapping(target = "pessoa", ignore = true)
  @Mapping(target = "role", source = "role")
  void updateFromDto(UsuarioUpdateDTO dto, @MappingTarget Usuario entity);

}
