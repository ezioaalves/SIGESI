package com.sigesi.sigesi.cemiterios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.EnderecoService;

@Service
public class CemiterioService {

  @Autowired
  private CemiterioRepository cemiterioRepository;

  @Autowired
  private EnderecoService enderecoService;

  public List<Cemiterio> getAll() {
    return cemiterioRepository.findAll();
  }

  public Cemiterio getCemiterioById(Long id) {
  return cemiterioRepository.findById(id)
    .orElseThrow(() -> new NotFoundException("Cemitério não encontrado com id " + id));
  }

  public Cemiterio createCemiterio(Cemiterio cemiterio) {
    if (cemiterio.getEndereco() != null && cemiterio.getEndereco().getId() != null) {
      enderecoService.getEnderecoById(cemiterio.getEndereco().getId());
    }
    return cemiterioRepository.save(cemiterio);
  }

  public Cemiterio updateCemiterio(Long id, Cemiterio cemiterioAtualizado) {
  Cemiterio cemiterio = cemiterioRepository.findById(id)
    .orElseThrow(() -> new NotFoundException("Cemitério não encontrado com id " + id));

    cemiterio.setNome(cemiterioAtualizado.getNome());

    if (cemiterioAtualizado.getEndereco() != null
        && cemiterioAtualizado.getEndereco().getId() != null) {
      enderecoService.getEnderecoById(cemiterioAtualizado.getEndereco().getId());
      cemiterio.setEndereco(cemiterioAtualizado.getEndereco());
    }

    return cemiterioRepository.save(cemiterio);
  }

  public void deleteCemiterio(Long id) {
  Cemiterio cemiterio = cemiterioRepository.findById(id)
    .orElseThrow(() -> new NotFoundException("Cemitério não encontrado com id " + id));

    cemiterioRepository.delete(cemiterio);
  }
}
