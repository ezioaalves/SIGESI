package com.sigesi.sigesi.demandas;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.solicitacoes.SolicitacaoMapper;

/**
 * Mapper MapStruct para Demanda.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {SolicitacaoMapper.class, DemandaMaterialMapper.class})
public interface DemandaMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "solicitacao", ignore = true)
  @Mapping(target = "responsavel", ignore = true)
  @Mapping(target = "materiais", ignore = true)
  @Mapping(target = "status", ignore = true)
  Demanda toEntity(DemandaCreateDTO dto);

  DemandaResponseDTO toDto(Demanda entity);

  List<DemandaResponseDTO> toDtoList(List<Demanda> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "solicitacao", ignore = true)
  @Mapping(target = "responsavel", ignore = true)
  @Mapping(target = "materiais", ignore = true)
  void updateFromDto(DemandaUpdateDTO dto, @MappingTarget Demanda entity);
}
