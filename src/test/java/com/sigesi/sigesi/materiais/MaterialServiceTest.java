package com.sigesi.sigesi.materiais;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Testes unitarios para MaterialService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialService Tests")
class MaterialServiceTest {

  @Mock
  private MaterialRepository materialRepository;

  @Mock
  private MaterialMapper materialMapper;

  @InjectMocks
  private MaterialService materialService;

  private Material material;
  private MaterialResponseDTO responseDTO;

  @BeforeEach
  void setUp() {
    material = Material.builder().id(1L).nome("Cimento").preco(50.0).build();
    responseDTO = MaterialResponseDTO.builder().id(1L).nome("Cimento").preco(50.0).build();
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando nao ha materiais")
  void testGetAllRetornaListaVazia() {
    when(materialRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

    List<MaterialResponseDTO> resultado = materialService.getAll();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(materialRepository, times(1)).findAllByOrderByIdAsc();
  }

  @Test
  @DisplayName("Deve retornar lista de materiais")
  void testGetAllRetornaLista() {
    Material m1 = mock(Material.class);
    Material m2 = mock(Material.class);

    when(materialRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(m1, m2));
    when(materialMapper.toDto(any())).thenReturn(mock(MaterialResponseDTO.class));

    List<MaterialResponseDTO> resultado = materialService.getAll();

    assertEquals(2, resultado.size());
  }

  @Test
  @DisplayName("Deve retornar material por ID")
  void testGetMaterialByIdComSucesso() {
    when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
    when(materialMapper.toDto(material)).thenReturn(responseDTO);

    MaterialResponseDTO resultado = materialService.getMaterialById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
    assertEquals("Cimento", resultado.getNome());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException quando ID nao encontrado")
  void testGetMaterialByIdNaoEncontrado() {
    when(materialRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> materialService.getMaterialById(999L));
  }

  @Test
  @DisplayName("Deve criar material com sucesso")
  void testCreateMaterialComSucesso() {
    MaterialCreateDTO createDTO = new MaterialCreateDTO("Cimento", 50.0);

    when(materialMapper.toEntity(createDTO)).thenReturn(material);
    when(materialRepository.save(material)).thenReturn(material);
    when(materialMapper.toDto(material)).thenReturn(responseDTO);

    MaterialResponseDTO resultado = materialService.createMaterial(createDTO);

    assertNotNull(resultado);
    verify(materialRepository, times(1)).save(material);
  }

  @Test
  @DisplayName("Deve atualizar material com sucesso")
  void testUpdateMaterialComSucesso() {
    MaterialUpdateDTO updateDTO = new MaterialUpdateDTO("Cimento Portland", 55.0);

    when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
    when(materialRepository.save(material)).thenReturn(material);
    when(materialMapper.toDto(material)).thenReturn(responseDTO);

    MaterialResponseDTO resultado = materialService.updateMaterial(1L, updateDTO);

    assertNotNull(resultado);
    verify(materialMapper, times(1)).updateFromDto(updateDTO, material);
    verify(materialRepository, times(1)).save(material);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao atualizar material inexistente")
  void testUpdateMaterialNaoEncontrado() {
    when(materialRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> materialService.updateMaterial(999L, new MaterialUpdateDTO()));
  }

  @Test
  @DisplayName("Deve deletar material com sucesso")
  void testDeleteMaterialComSucesso() {
    when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

    materialService.deleteMaterial(1L);

    verify(materialRepository, times(1)).delete(material);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException ao deletar material inexistente")
  void testDeleteMaterialNaoEncontrado() {
    when(materialRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> materialService.deleteMaterial(999L));
    verify(materialRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Deve retornar entidade Material por ID")
  void testGetMaterialEntityByIdComSucesso() {
    when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

    Material resultado = materialService.getMaterialEntityById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para entidade inexistente")
  void testGetMaterialEntityByIdNaoEncontrado() {
    when(materialRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> materialService.getMaterialEntityById(999L));
  }

  @Test
  @DisplayName("Deve buscar multiplos materiais por IDs com sucesso")
  void testFindAllByIdsComSucesso() {
    Material m1 = Material.builder().id(1L).nome("Cimento").preco(50.0).build();
    Material m2 = Material.builder().id(2L).nome("Areia").preco(30.0).build();
    Set<Long> ids = Set.of(1L, 2L);

    when(materialRepository.findAllByIdIn(ids)).thenReturn(Set.of(m1, m2));

    Set<Material> resultado = materialService.findAllByIds(ids);

    assertEquals(2, resultado.size());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException quando IDs nao encontrados")
  void testFindAllByIdsComIdInexistente() {
    Set<Long> ids = Set.of(1L, 2L, 3L);

    when(materialRepository.findAllByIdIn(ids)).thenReturn(Set.of(material));

    assertThrows(NotFoundException.class,
        () -> materialService.findAllByIds(ids));
  }
}
