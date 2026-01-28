package com.sigesi.sigesi.materiais;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.materiais.dtos.MaterialCreateDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialResponseDTO;
import com.sigesi.sigesi.materiais.dtos.MaterialUpdateDTO;

/**
 * Testes para MaterialController.
 */
@WebMvcTest(controllers = MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MaterialController Tests")
class MaterialControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MaterialService service;

  private MaterialResponseDTO responseDto(Long id, String nome, Double preco) {
    return MaterialResponseDTO.builder()
        .id(id)
        .nome(nome)
        .preco(preco)
        .build();
  }

  @Test
  @DisplayName("GET /api/materiais/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/materiais/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/materiais/ retorna 200 com multiplos materiais")
  void testListAllRetorna200ComMultiplosMateriais() throws Exception {
    MaterialResponseDTO dto1 = responseDto(1L, "Cimento", 50.0);
    MaterialResponseDTO dto2 = responseDto(2L, "Areia", 30.0);

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/materiais/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nome", is("Cimento")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  @DisplayName("GET /api/materiais/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200QuandoEncontrado() throws Exception {
    MaterialResponseDTO dto = responseDto(1L, "Cimento", 50.0);

    given(service.getMaterialById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/materiais/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cimento")))
        .andExpect(jsonPath("$.preco", is(50.0)));
  }

  @Test
  @DisplayName("GET /api/materiais/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(service.getMaterialById(999L))
        .willThrow(new NotFoundException("Material não encontrado com id 999"));

    mockMvc.perform(get("/api/materiais/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/materiais/ retorna 201 quando criado com sucesso")
  void testCreateRetorna201QuandoCriadoComSucesso() throws Exception {
    MaterialCreateDTO createDto = new MaterialCreateDTO("Cimento", 50.0);
    MaterialResponseDTO responseDto = responseDto(1L, "Cimento", 50.0);

    given(service.createMaterial(any(MaterialCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/materiais/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cimento")));
  }

  @Test
  @DisplayName("POST /api/materiais/ retorna 400 quando nome vazio")
  void testCreateRetorna400QuandoNomeVazio() throws Exception {
    MaterialCreateDTO createDto = new MaterialCreateDTO("", 50.0);

    mockMvc.perform(post("/api/materiais/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/materiais/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDadosAtualizados() throws Exception {
    MaterialUpdateDTO updateDto = new MaterialUpdateDTO("Cimento Portland", 55.0);
    MaterialResponseDTO responseDto = responseDto(1L, "Cimento Portland", 55.0);

    given(service.updateMaterial(eq(1L), any(MaterialUpdateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(patch("/api/materiais/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome", is("Cimento Portland")))
        .andExpect(jsonPath("$.preco", is(55.0)));
  }

  @Test
  @DisplayName("DELETE /api/materiais/{id} retorna 204 quando deletado")
  void testDeleteRetorna204QuandoDeletado() throws Exception {
    doNothing().when(service).deleteMaterial(1L);

    mockMvc.perform(delete("/api/materiais/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/materiais/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Material não encontrado com id 999"))
        .when(service).deleteMaterial(999L);

    mockMvc.perform(delete("/api/materiais/999"))
        .andExpect(status().isNotFound());
  }
}
