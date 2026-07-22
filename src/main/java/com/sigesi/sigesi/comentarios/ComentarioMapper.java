package com.sigesi.sigesi.comentarios;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;

/**
 * Mapper MapStruct para Comentario.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ComentarioMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "demanda", ignore = true)
  @Mapping(target = "autor", ignore = true)
  @Mapping(target = "criadoEm", ignore = true)
  Comentario toEntity(ComentarioCreateDTO dto);

  @Mapping(target = "demandaId", source = "demanda.id")
  ComentarioResponseDTO toDto(Comentario entity);

  List<ComentarioResponseDTO> toDtoList(List<Comentario> entities);
}
