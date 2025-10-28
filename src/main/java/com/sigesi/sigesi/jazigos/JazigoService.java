package com.sigesi.sigesi.jazigos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.cemiterios.CemiterioService;

@Service
public class JazigoService {

  @Autowired
  private JazigoRepository jazigoRepository;

  @Autowired
  private CemiterioService cemiterioService;

  public List<Jazigo> getAll() {
    return jazigoRepository.findAll();
  }

  public Jazigo getJazigoById(Long id) {
    return jazigoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Jazigo não encontrado com id " + id));
  }

  public Jazigo createJazigo(Jazigo jazigo) {
    if (jazigo.getCemiterio() != null && jazigo.getCemiterio().getId() != null) {
      cemiterioService.getCemiterioById(jazigo.getCemiterio().getId());
    }
    return jazigoRepository.save(jazigo);
  }

  public Jazigo updateJazigo(Long id, Jazigo jazigoAtualizado) {
    Jazigo jazigo = jazigoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Jazigo não encontrado com id " + id));

    jazigo.setLargura(jazigoAtualizado.getLargura());
    jazigo.setComprimento(jazigoAtualizado.getComprimento());
    jazigo.setQuadra(jazigoAtualizado.getQuadra());
    jazigo.setRua(jazigoAtualizado.getRua());
    jazigo.setLote(jazigoAtualizado.getLote());

    if (jazigoAtualizado.getCemiterio() != null
        && jazigoAtualizado.getCemiterio().getId() != null) {
      cemiterioService.getCemiterioById(jazigoAtualizado.getCemiterio().getId());
      jazigo.setCemiterio(jazigoAtualizado.getCemiterio());
    }

    return jazigoRepository.save(jazigo);
  }

  public void deleteJazigo(Long id) {
    Jazigo jazigo = jazigoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Jazigo não encontrado com id " + id));

    jazigoRepository.delete(jazigo);
  }
}
