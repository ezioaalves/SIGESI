package com.sigesi.sigesi.gavetas;

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
import com.sigesi.sigesi.gavetas.dtos.GavetaCreateDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaResponseDTO;
import com.sigesi.sigesi.gavetas.dtos.GavetaUpdateDTO;

@WebMvcTest(controllers = GavetaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GavetaController Tests")
class GavetaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GavetaService gavetaService;

  // ===== Métodos auxiliares =====
  private GavetaResponseDTO gavetaDTO(Long id, Integer numero) {
    GavetaResponseDTO dto = new GavetaResponseDTO();
    dto.setId(id);
    dto.setJazigo(1L);
    dto.setNumero(numero);
    dto.setOcupante(1L);
    return dto;
  }

  private GavetaCreateDTO createDTO(Long jazigoId, Integer numero, Long ocupanteId) {
    GavetaCreateDTO dto = new GavetaCreateDTO();
    dto.setJazigo(jazigoId);
    dto.setNumero(numero);
    dto.setOcupante(ocupanteId);
    return dto;
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/gavetas/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(gavetaService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/gavetas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/gavetas/ retorna 200 com gavetas")
  void testListAllRetorna200ComGavetas() throws Exception {
    var g1 = gavetaDTO(1L, 1);
    var g2 = gavetaDTO(2L, 2);
    given(gavetaService.getAll()).willReturn(List.of(g1, g2));

    mockMvc.perform(get("/api/gavetas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].numero", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].numero", is(2)));
  }

  @Test
  @DisplayName("GET /api/gavetas/{id} retorna 200 com gaveta encontrada")
  void testGetByIdRetorna200ComGaveta() throws Exception {
    var g1 = gavetaDTO(1L, 1);
    given(gavetaService.getGavetaById(1L)).willReturn(g1);

    mockMvc.perform(get("/api/gavetas/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.numero", is(1)));
  }

  @Test
  @DisplayName("GET /api/gavetas/{id} retorna 404 quando não encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(gavetaService.getGavetaById(999L))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Gaveta não encontrada com id 999"));

    mockMvc.perform(get("/api/gavetas/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  // ===== POST =====
  @Test
  @DisplayName("POST /api/gavetas/ retorna 201 ao criar com sucesso")
  void testCreateRetorna201() throws Exception {
    var request = createDTO(1L, 1, 1L);
    var response = gavetaDTO(1L, 1);

    given(gavetaService.createGaveta(any(GavetaCreateDTO.class))).willReturn(response);

    mockMvc.perform(post("/api/gavetas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.numero", is(1)));
  }

  @Test
  @DisplayName("POST /api/gavetas/ retorna 400 quando dados inválidos")
  void testCreateRetorna400() throws Exception {
    var request = createDTO(null, null, null);

    mockMvc.perform(post("/api/gavetas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== PATCH =====
  @Test
  @DisplayName("PATCH /api/gavetas/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200() throws Exception {
    GavetaUpdateDTO request = new GavetaUpdateDTO();
    request.setNumero(2);
    request.setJazigo(2L);

    var response = gavetaDTO(1L, 2);

    given(gavetaService.updateGaveta(eq(1L), any(GavetaUpdateDTO.class))).willReturn(response);

    mockMvc.perform(patch("/api/gavetas/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.numero", is(2)));
  }

  @Test
  @DisplayName("PATCH /api/gavetas/{id} retorna 404 quando gaveta não existe")
  void testUpdateRetorna404() throws Exception {
    GavetaUpdateDTO request = new GavetaUpdateDTO();
    request.setNumero(2);

    given(gavetaService.updateGaveta(eq(999L), any(GavetaUpdateDTO.class)))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Gaveta não encontrada com id 999"));

    mockMvc.perform(patch("/api/gavetas/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ===== DELETE =====
  @Test
  @DisplayName("DELETE /api/gavetas/{id} retorna 204 quando exclui com sucesso")
  void testDeleteRetorna204() throws Exception {
    doNothing().when(gavetaService).deleteGaveta(1L);

    mockMvc.perform(delete("/api/gavetas/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/gavetas/{id} retorna 404 quando não encontrado")
  void testDeleteRetorna404() throws Exception {
    doThrow(new com.sigesi.sigesi.config.NotFoundException("Gaveta não encontrada com id 999"))
        .when(gavetaService).deleteGaveta(999L);

    mockMvc.perform(delete("/api/gavetas/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
