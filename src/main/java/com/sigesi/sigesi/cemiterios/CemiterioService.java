package com.sigesi.sigesi.cemiterios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.cemiterios.dtos.CemiterioCreateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioResponseDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioUpdateDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.enderecos.EnderecoService;

@Service
public class CemiterioService {

  @Autowired
  private CemiterioRepository cemiterioRepository;

  @Autowired
  private EnderecoService enderecoService;

  @Autowired
  private CemiterioMapper cemiterioMapper;

  public List<CemiterioResponseDTO> getAll() {
    return cemiterioRepository.findAllByOrderByIdAsc()
        .stream()
        .map(cemiterioMapper::toDto)
        .collect(Collectors.toList());
  }

  public CemiterioResponseDTO getCemiterioById(Long id) {
    Cemiterio cemiterio = this.getCemiterioEntityById(id);

    return cemiterioMapper.toDto(cemiterio);
  }

  public CemiterioResponseDTO createCemiterio(CemiterioCreateDTO cemiterioDto) {
    Endereco endereco = enderecoService.getEnderecoEntityById(cemiterioDto.getEndereco());

    Cemiterio cemiterio = cemiterioMapper.toEntity(cemiterioDto);
    cemiterio.setEndereco(endereco);
    cemiterioRepository.save(cemiterio);

    return cemiterioMapper.toDto(cemiterio);

  }

  public CemiterioResponseDTO updateCemiterio(Long id, CemiterioUpdateDTO cemiterioDto) {
    Cemiterio cemiterio = this.getCemiterioEntityById(id);

    cemiterioMapper.updateFromDto(cemiterioDto, cemiterio);

    if (cemiterioDto.getEndereco() != null) {
      Endereco endereco = enderecoService.getEnderecoEntityById(cemiterioDto.getEndereco());
      cemiterio.setEndereco(endereco);
    }

    cemiterioRepository.save(cemiterio);

    return cemiterioMapper.toDto(cemiterio);
  }

  public void deleteCemiterio(Long id) {
    Cemiterio cemiterio = this.getCemiterioEntityById(id);

    cemiterioRepository.delete(cemiterio);
  }

  public Cemiterio getCemiterioEntityById(Long id) {
    return cemiterioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Cemiterio não econtrado com id " + id));
  }
}
