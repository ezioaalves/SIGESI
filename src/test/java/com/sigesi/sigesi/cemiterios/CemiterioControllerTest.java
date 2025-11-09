package com.sigesi.sigesi.cemiterios;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioCreateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioUpdateDTO;
import com.sigesi.sigesi.cemiterios.dtos.CemiterioResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;

@WebMvcTest(controllers = CemiterioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CemiterioController Tests")
class CemiterioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CemiterioService cemiterioService;

  // ===== Métodos auxiliares =====
  private EnderecoResponseDTO enderecoDTO(Long id) {
    return EnderecoResponseDTO.builder()
        .id(id)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();
  }

  private CemiterioResponseDTO cemiterioDTO(Long id, String nome, Long enderecoId) {
    return CemiterioResponseDTO.builder()
        .id(id)
        .nome(nome)
        .endereco(enderecoDTO(enderecoId))
        .build();
  }

  private CemiterioCreateDTO createDTO(String nome, Long enderecoId) {
    return CemiterioCreateDTO.builder()
        .nome(nome)
        .endereco(enderecoId)
        .build();
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/cemiterios/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(cemiterioService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/cemiterios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/cemiterios/ retorna 200 com cemitérios")
  void testListAllRetorna200ComCemiterios() throws Exception {
    var c1 = cemiterioDTO(1L, "Cemitério Central", 1L);
    var c2 = cemiterioDTO(2L, "Cemitério Municipal", 1L);
    given(cemiterioService.getAll()).willReturn(List.of(c1, c2));

    mockMvc.perform(get("/api/cemiterios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nome", is("Cemitério Central")))
        .andExpect(jsonPath("$[0].endereco.id", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].nome", is("Cemitério Municipal")));
  }

  @Test
  @DisplayName("GET /api/cemiterios/{id} retorna 200 com cemitério encontrado")
  void testGetByIdRetorna200ComCemiterio() throws Exception {
    var c1 = cemiterioDTO(1L, "Cemitério Central", 1L);
    given(cemiterioService.getCemiterioById(1L)).willReturn(c1);

    mockMvc.perform(get("/api/cemiterios/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cemitério Central")))
        .andExpect(jsonPath("$.endereco.id", is(1)));
  }

  @Test
  @DisplayName("GET /api/cemiterios/{id} retorna 404 quando não encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(cemiterioService.getCemiterioById(999L))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Cemitério não encontrado com id 999"));

    mockMvc.perform(get("/api/cemiterios/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  // ===== POST =====
  @Test
  @DisplayName("POST /api/cemiterios/ retorna 201 ao criar com sucesso")
  void testCreateRetorna201() throws Exception {
    var request = createDTO("Cemitério Central", 1L);
    var response = cemiterioDTO(1L, "Cemitério Central", 1L);

    given(cemiterioService.createCemiterio(any(CemiterioCreateDTO.class))).willReturn(response);

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cemitério Central")))
        .andExpect(jsonPath("$.endereco.id", is(1)));
  }

  @Test
  @DisplayName("POST /api/cemiterios/ retorna 400 quando dados inválidos")
  void testCreateRetorna400() throws Exception {
    var request = createDTO("", null);

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== PUT =====
  @Test
  @DisplayName("PATCH /api/cemiterios/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200() throws Exception {
    var request = createDTO("Novo Nome", 2L);
    var response = cemiterioDTO(1L, "Novo Nome", 2L);

    given(cemiterioService.updateCemiterio(eq(1L), any(CemiterioUpdateDTO.class))).willReturn(response);

    mockMvc.perform(patch("/api/cemiterios/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Novo Nome")))
        .andExpect(jsonPath("$.endereco.id", is(2)));
  }

  @Test
  @DisplayName("PATCH /api/cemiterios/{id} retorna 404 quando cemitério não existe")
  void testUpdateRetorna404() throws Exception {
    var request = createDTO("Novo Nome", 2L);

    given(cemiterioService.updateCemiterio(eq(999L), any(CemiterioUpdateDTO.class)))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Cemitério não encontrado com id 999"));

    mockMvc.perform(patch("/api/cemiterios/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ===== DELETE =====
  @Test
  @DisplayName("DELETE /api/cemiterios/{id} retorna 204 quando exclui com sucesso")
  void testDeleteRetorna204() throws Exception {
    doNothing().when(cemiterioService).deleteCemiterio(1L);

    mockMvc.perform(delete("/api/cemiterios/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/cemiterios/{id} retorna 404 quando não encontrado")
  void testDeleteRetorna404() throws Exception {
    doThrow(new com.sigesi.sigesi.config.NotFoundException("Cemitério não encontrado com id 999"))
        .when(cemiterioService).deleteCemiterio(999L);

    mockMvc.perform(delete("/api/cemiterios/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
