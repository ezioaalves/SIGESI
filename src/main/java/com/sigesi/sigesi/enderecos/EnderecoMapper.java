package com.sigesi.sigesi.enderecos;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EnderecoMapper {

  @Mapping(target = "id", ignore = true)
  Endereco toEntity(EnderecoCreateDTO dto);

  EnderecoResponseDTO toDto(Endereco entity);

  List<EnderecoResponseDTO> toDtoList(List<Endereco> entities);

  @Mapping(target = "id", ignore = true)
  void updateFromDto(EnderecoUpdateDTO dto, @MappingTarget Endereco endereco);
}
