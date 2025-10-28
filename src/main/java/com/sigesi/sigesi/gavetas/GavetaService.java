package com.sigesi.sigesi.gavetas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.jazigos.JazigoService;
import com.sigesi.sigesi.pessoas.PessoaService;

@Service
public class GavetaService {

  @Autowired
  private GavetaRepository gavetaRepository;

  @Autowired
  private JazigoService jazigoService;

  @Autowired
  private PessoaService pessoaService;

  public List<Gaveta> getAll() {
    return gavetaRepository.findAll();
  }

  public Gaveta getGavetaById(Long id) {
    return gavetaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gaveta não encontrada com id " + id));
  }

  public Gaveta createGaveta(Gaveta gaveta) {
    if (gaveta.getJazigo() != null && gaveta.getJazigo().getId() != null) {
      jazigoService.getJazigoById(gaveta.getJazigo().getId());
    }
    if (gaveta.getOcupante() != null && gaveta.getOcupante().getId() != null) {
      pessoaService.getPessoaById(gaveta.getOcupante().getId());
    }
    return gavetaRepository.save(gaveta);
  }

  public Gaveta updateGaveta(Long id, Gaveta gavetaAtualizada) {
    Gaveta gaveta = gavetaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gaveta não encontrada com id " + id));

    gaveta.setNumero(gavetaAtualizada.getNumero());

    if (gavetaAtualizada.getJazigo() != null
        && gavetaAtualizada.getJazigo().getId() != null) {
      jazigoService.getJazigoById(gavetaAtualizada.getJazigo().getId());
      gaveta.setJazigo(gavetaAtualizada.getJazigo());
    }

    if (gavetaAtualizada.getOcupante() != null
        && gavetaAtualizada.getOcupante().getId() != null) {
      pessoaService.getPessoaById(gavetaAtualizada.getOcupante().getId());
      gaveta.setOcupante(gavetaAtualizada.getOcupante());
    } else {
      gaveta.setOcupante(null);
    }

    return gavetaRepository.save(gaveta);
  }

  public void deleteGaveta(Long id) {
    Gaveta gaveta = gavetaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gaveta não encontrada com id " + id));

    gavetaRepository.delete(gaveta);
  }
}
