package com.sigesi.sigesi.solicitacoes;

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
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SolicitacaoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "autor", source = "autorId")
  @Mapping(target = "local", source = "localId")
  Solicitacao toEntity(SolicitacaoCreateDTO dto);

  SolicitacaoResponseDTO toDto(Solicitacao entity);

  List<SolicitacaoResponseDTO> toDtoList(List<Solicitacao> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "autor", source = "autorId")
  @Mapping(target = "local", source = "localId")
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
}
