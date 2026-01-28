package com.sigesi.sigesi.materiais;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Service para Material.
 */
@Service
public class MaterialService {

  @Autowired
  private MaterialRepository materialRepository;

  @Autowired
  private MaterialMapper materialMapper;

  /**
   * Lista todos os materiais ordenados por ID.
   */
  public List<MaterialResponseDTO> getAll() {
    return materialRepository.findAllByOrderByIdAsc()
        .stream()
        .map(materialMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Busca material por ID.
   */
  public MaterialResponseDTO getMaterialById(Long id) {
    Material material = this.getMaterialEntityById(id);
    return materialMapper.toDto(material);
  }

  /**
   * Cria novo material.
   */
  public MaterialResponseDTO createMaterial(MaterialCreateDTO dto) {
    Material entity = materialMapper.toEntity(dto);
    Material saved = materialRepository.save(entity);
    return materialMapper.toDto(saved);
  }

  /**
   * Atualiza material existente.
   */
  public MaterialResponseDTO updateMaterial(Long id, MaterialUpdateDTO dto) {
    Material material = this.getMaterialEntityById(id);
    materialMapper.updateFromDto(dto, material);
    Material updated = materialRepository.save(material);
    return materialMapper.toDto(updated);
  }

  /**
   * Deleta material por ID.
   */
  public void deleteMaterial(Long id) {
    Material material = this.getMaterialEntityById(id);
    materialRepository.delete(material);
  }

  /**
   * Busca entidade Material por ID (uso interno).
   */
  public Material getMaterialEntityById(Long id) {
    return materialRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Material não encontrado com id " + id));
  }

  /**
   * Busca multiplos materiais por IDs (para relacionamento M:N).
   */
  public Set<Material> findAllByIds(Set<Long> ids) {
    Set<Material> materiais = materialRepository.findAllByIdIn(ids);
    if (materiais.size() != ids.size()) {
      throw new NotFoundException("Um ou mais materiais não foram encontrados");
    }
    return materiais;
  }
}
