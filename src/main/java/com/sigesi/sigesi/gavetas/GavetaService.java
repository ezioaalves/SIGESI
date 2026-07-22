package com.sigesi.sigesi.gavetas;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.gavetas.dtos.GavetaCreateDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaResponseDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaUpdateDTO;
import com.sigesi.sigesi.jazigos.Jazigo;
import com.sigesi.sigesi.jazigos.JazigoService;
import com.sigesi.sigesi.pessoas.Pessoa;
import com.sigesi.sigesi.pessoas.PessoaService;

@Service
public class GavetaService {

  @Autowired
  private GavetaRepository gavetaRepository;

  @Autowired
  private GavetaMapper gavetaMapper;

  @Autowired
  private JazigoService jazigoService;

  @Autowired
  private PessoaService pessoaService;

  public List<GavetaResponseDTO> getAll() {
    return gavetaRepository.findAllByOrderByIdAsc()
        .stream()
        .map(gavetaMapper::toDto)
        .collect(Collectors.toList());
  }

  public List<GavetaResponseDTO> getAllFiltered(Long jazigoId, Long ocupanteId) {
    List<Gaveta> gavetas;

    if (jazigoId != null && ocupanteId != null) {
      gavetas = gavetaRepository.findByJazigoIdAndOcupanteId(jazigoId, ocupanteId);
    } else if (jazigoId != null) {
      gavetas = gavetaRepository.findByJazigoId(jazigoId);
    } else if (ocupanteId != null) {
      gavetas = gavetaRepository.findByOcupanteId(ocupanteId);
    } else {
      gavetas = gavetaRepository.findAll();
    }

    return gavetas.stream()
        .map(gavetaMapper::toDto)
        .toList();

  }

  public GavetaResponseDTO getGavetaById(Long id) {
    Gaveta gaveta = this.getGavetaEntityById(id);

    return gavetaMapper.toDto(gaveta);
  }

  public GavetaResponseDTO createGaveta(GavetaCreateDTO gavetaDto) {
    Pessoa ocupante = pessoaService.getPessoaEntityById(gavetaDto.getOcupante());
    Jazigo jazigo = jazigoService.getJazigoEntityById(gavetaDto.getJazigo());

    Gaveta gaveta = gavetaMapper.toEntity(gavetaDto);
    gaveta.setOcupante(ocupante);
    gaveta.setJazigo(jazigo);

    return gavetaMapper.toDto(gavetaRepository.save(gaveta));
  }

  public GavetaResponseDTO updateGaveta(Long id, GavetaUpdateDTO gavetaDto) {

    Gaveta gaveta = this.getGavetaEntityById(id);

    if (gavetaDto.getJazigo() != null) {
      Jazigo jazigo = jazigoService.getJazigoEntityById(gavetaDto.getJazigo());
      gaveta.setJazigo(jazigo);
    }

    if (gavetaDto.getOcupante() != null) {
      Pessoa ocupante = pessoaService.getPessoaEntityById(gavetaDto.getOcupante());
      gaveta.setOcupante(ocupante);
    }

    gavetaMapper.updateFromDto(gavetaDto, gaveta);

    return gavetaMapper.toDto(gavetaRepository.save(gaveta));
  }

  public void deleteGaveta(Long id) {
    Gaveta gaveta = this.getGavetaEntityById(id);

    gavetaRepository.delete(gaveta);
  }

  public Gaveta getGavetaEntityById(Long id) {
    return gavetaRepository.findById(id).orElseThrow(() -> new NotFoundException("Gaveta não econtrada com id " + id));
  }

}
