package com.sigesi.sigesi.documentos;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoResponseDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoUpdateDTO;
import com.sigesi.sigesi.pessoas.Pessoa;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para Documento.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "assinante", source = "assinanteId", qualifiedByName = "mapAssinante")
  @Mapping(target = "interessado", source = "interessadoId", qualifiedByName = "mapInteressado")
  @Mapping(target = "anexos", source = "anexoIds")
  Documento toEntity(DocumentoCreateDTO dto);

  DocumentoResponseDTO toDto(Documento entity);

  List<DocumentoResponseDTO> toDtoList(List<Documento> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "assinante", ignore = true)
  @Mapping(target = "interessado", ignore = true)
  @Mapping(target = "anexos", ignore = true)
  void updateFromDto(DocumentoUpdateDTO dto, @MappingTarget Documento documento);

  @Named("mapAssinante")
  default Pessoa mapAssinante(Long assinanteId) {
    if (assinanteId == null) {
      return null;
    }
    return Pessoa.builder().id(assinanteId).build();
  }

  @Named("mapInteressado")
  default Pessoa mapInteressado(Long interessadoId) {
    if (interessadoId == null) {
      return null;
    }
    return Pessoa.builder().id(interessadoId).build();
  }

  default List<Arquivo> mapAnexos(List<Long> anexoIds) {
    if (anexoIds == null || anexoIds.isEmpty()) {
      return null;
    }
    return anexoIds.stream()
        .map(id -> Arquivo.builder().id(id).build())
        .collect(java.util.stream.Collectors.toList());
  }
}
