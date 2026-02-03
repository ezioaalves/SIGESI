package com.sigesi.sigesi.solicitacoes;

import com.sigesi.sigesi.arquivos.Arquivo;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;
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
  @Mapping(target = "autor", source = "autorId")
  @Mapping(target = "local", source = "localId")
  @Mapping(target = "anexos", source = "anexoIds")
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
  void updateFromDto(SolicitacaoUpdateDTO dto, @MappingTarget Solicitacao solicitacao);

  default Usuario mapAutor(Long autorId) {
    if (autorId == null) {
      return null;
    }
    return Usuario.builder().id(autorId).build();
  }

  default Endereco mapLocal(Long localId) {
    if (localId == null) {
      return null;
    }
    return Endereco.builder().id(localId).build();
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
