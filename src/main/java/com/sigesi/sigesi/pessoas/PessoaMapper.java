package com.sigesi.sigesi.pessoas;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PessoaMapper {

  @Mapping(target = "endereco", ignore = true)
  @Mapping(target = "id", ignore = true)
  Pessoa toEntity(PessoaCreateDTO dto);

  PessoaResponseDTO toDto(Pessoa pessoa);

  @Mapping(target = "endereco", ignore = true)
  @Mapping(target = "id", ignore = true)
  void updateFromDto(PessoaUpdateDTO dto, @MappingTarget Pessoa entity);

}
