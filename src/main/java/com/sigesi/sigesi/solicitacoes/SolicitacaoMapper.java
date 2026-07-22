package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para Solicitacao.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SolicitacaoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "autor", ignore = true)
  @Mapping(target = "solicitante", ignore = true)
  @Mapping(target = "local", ignore = true)
  @Mapping(target = "anexos", ignore = true)
  Solicitacao toEntity(SolicitacaoCreateDTO dto);

  SolicitacaoResponseDTO toDto(Solicitacao entity);

  List<SolicitacaoResponseDTO> toDtoList(List<Solicitacao> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "status", source = "status")
  @Mapping(target = "local", ignore = true)
  @Mapping(target = "anexos", ignore = true)
  @Mapping(target = "assunto", ignore = true)
  @Mapping(target = "body", ignore = true)
  @Mapping(target = "autor", ignore = true)
  @Mapping(target = "solicitante", ignore = true)
  void updateFromDto(SolicitacaoUpdateDTO dto, @MappingTarget Solicitacao solicitacao);
}
