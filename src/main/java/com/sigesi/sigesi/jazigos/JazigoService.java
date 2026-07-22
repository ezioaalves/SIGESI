package com.sigesi.sigesi.jazigos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.cemiterios.Cemiterio;
import com.sigesi.sigesi.cemiterios.CemiterioService;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.jazigos.dtos.JazigoCreateDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoResponseDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoUpdateDTO;

@Service
public class JazigoService {

  @Autowired
  private JazigoRepository jazigoRepository;

  @Autowired
  private CemiterioService cemiterioService;

  @Autowired
  private JazigoMapper jazigoMapper;

  public List<JazigoResponseDTO> getAll() {
    return jazigoRepository.findAllByOrderByIdAsc()
        .stream()
        .map(jazigoMapper::toDto)
        .collect(Collectors.toList());
  }

  public JazigoResponseDTO getJazigoById(Long id) {
    Jazigo jazigo = this.getJazigoEntityById(id);

    return jazigoMapper.toDto(jazigo);
  }

  public JazigoResponseDTO createJazigo(JazigoCreateDTO jazigoDto) {

    Cemiterio cemiterio = cemiterioService.getCemiterioEntityById(jazigoDto.getCemiterio());

    Jazigo jazigo = jazigoMapper.toEntity(jazigoDto);
    jazigo.setCemiterio(cemiterio);

    jazigoRepository.save(jazigo);

    return jazigoMapper.toDto(jazigo);
  }

  public JazigoResponseDTO updateJazigo(Long id, JazigoUpdateDTO jazigoDto) {
    Jazigo jazigo = this.getJazigoEntityById(id);

    jazigoMapper.updateFromDto(jazigoDto, jazigo);

    if (jazigoDto.getCemiterio() != null) {
      Cemiterio cemiterio = cemiterioService.getCemiterioEntityById(jazigoDto.getCemiterio());
      jazigo.setCemiterio(cemiterio);
    }

    jazigoRepository.save(jazigo);
    return jazigoMapper.toDto(jazigo);
  }

  public void deleteJazigo(Long id) {
    Jazigo jazigo = this.getJazigoEntityById(id);

    jazigoRepository.delete(jazigo);
  }

  public Jazigo getJazigoEntityById(Long id) {
    return jazigoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Jazigo não encontrado com id " + id));
  }

}
