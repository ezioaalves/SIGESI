package com.sigesi.sigesi.arquivos;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for Arquivo.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArquivoMapper {

  ArquivoResponseDTO toDto(Arquivo arquivo);

  List<ArquivoResponseDTO> toDtoList(List<Arquivo> arquivos);
}
