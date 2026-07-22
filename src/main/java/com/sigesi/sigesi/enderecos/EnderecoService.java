package com.sigesi.sigesi.enderecos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoUpdateDTO;

@Service
public class EnderecoService {

  @Autowired
  private EnderecoRepository enderecoRepository;

  @Autowired
  private EnderecoMapper enderecoMapper;

  public List<EnderecoResponseDTO> getAll() {
    return enderecoRepository.findAllByOrderByIdAsc()
        .stream()
        .map(enderecoMapper::toDto)
        .collect(Collectors.toList());
  }

  public EnderecoResponseDTO getEnderecoById(Long id) {
    Endereco endereco = this.getEnderecoEntityById(id);

    return enderecoMapper.toDto(endereco);
  }

  public EnderecoResponseDTO createEndereco(EnderecoCreateDTO endereco) {
    Endereco entity = enderecoMapper.toEntity(endereco);
    Endereco enderecoSalvo = enderecoRepository.save(entity);
    return enderecoMapper.toDto(enderecoSalvo);
  }

  public EnderecoResponseDTO updateEndereco(Long id, EnderecoUpdateDTO enderecoDTO) {
    Endereco endereco = this.getEnderecoEntityById(id);

    enderecoMapper.updateFromDto(enderecoDTO, endereco);
    Endereco enderecoAtualizado = enderecoRepository.save(endereco);
    return enderecoMapper.toDto(enderecoAtualizado);
  }

  public void deleteEndereco(Long id) {
    Endereco endereco = this.getEnderecoEntityById(id);

    enderecoRepository.delete(endereco);
  }

  public Endereco getEnderecoEntityById(Long id) {
    return enderecoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Endereço não encontrado com id " + id));
  }
}
