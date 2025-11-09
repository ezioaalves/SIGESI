package com.sigesi.sigesi.jazigos;

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
import com.sigesi.sigesi.cemiterios.Cemiterio;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.jazigos.dtos.JazigoCreateDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoResponseDTO;
import com.sigesi.sigesi.jazigos.dtos.JazigoUpdateDTO;

@WebMvcTest(controllers = JazigoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("JazigoController Tests")
class JazigoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JazigoService jazigoService;

  // ===== Métodos auxiliares =====
  private Cemiterio cemiterioMock() {
    Endereco endereco = Endereco.builder()
        .id(1L)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();

    return Cemiterio.builder()
        .id(1L)
        .nome("Cemitério Central")
        .endereco(endereco)
        .build();
  }

  private JazigoResponseDTO jazigoDTO(Long id, Integer quadra, String rua, String lote) {
    JazigoResponseDTO dto = new JazigoResponseDTO();
    dto.setId(id);
    dto.setCemiterio(cemiterioMock());
    dto.setQuadra(quadra);
    dto.setRua(rua);
    dto.setLote(lote);
    return dto;
  }

  private JazigoCreateDTO createDTO(Long cemiterioId, Integer quadra, String rua, String lote) {
    JazigoCreateDTO dto = new JazigoCreateDTO();
    dto.setCemiterio(cemiterioId);
    dto.setQuadra(quadra);
    dto.setRua(rua);
    dto.setLote(lote);
    return dto;
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/jazigos/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(jazigoService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/jazigos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/jazigos/ retorna 200 com jazigos")
  void testListAllRetorna200ComJazigos() throws Exception {
    var j1 = jazigoDTO(1L, 1, "A", "10");
    var j2 = jazigoDTO(2L, 2, "B", "20");
    given(jazigoService.getAll()).willReturn(List.of(j1, j2));

    mockMvc.perform(get("/api/jazigos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].quadra", is(1)))
        .andExpect(jsonPath("$[0].rua", is("A")))
        .andExpect(jsonPath("$[0].lote", is("10")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].quadra", is(2)));
  }

  @Test
  @DisplayName("GET /api/jazigos/{id} retorna 200 com jazigo encontrado")
  void testGetByIdRetorna200ComJazigo() throws Exception {
    var j1 = jazigoDTO(1L, 1, "A", "10");
    given(jazigoService.getJazigoById(1L)).willReturn(j1);

    mockMvc.perform(get("/api/jazigos/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.quadra", is(1)))
        .andExpect(jsonPath("$.rua", is("A")))
        .andExpect(jsonPath("$.lote", is("10")));
  }

  @Test
  @DisplayName("GET /api/jazigos/{id} retorna 404 quando não encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(jazigoService.getJazigoById(999L))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Jazigo não encontrado com id 999"));

    mockMvc.perform(get("/api/jazigos/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  // ===== POST =====
  @Test
  @DisplayName("POST /api/jazigos/ retorna 201 ao criar com sucesso")
  void testCreateRetorna201() throws Exception {
    var request = createDTO(1L, 1, "A", "10");
    var response = jazigoDTO(1L, 1, "A", "10");

    given(jazigoService.createJazigo(any(JazigoCreateDTO.class))).willReturn(response);

    mockMvc.perform(post("/api/jazigos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.quadra", is(1)))
        .andExpect(jsonPath("$.rua", is("A")))
        .andExpect(jsonPath("$.lote", is("10")));
  }

  @Test
  @DisplayName("POST /api/jazigos/ retorna 400 quando dados inválidos")
  void testCreateRetorna400() throws Exception {
    var request = createDTO(null, null, "", "");

    mockMvc.perform(post("/api/jazigos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== PATCH =====
  @Test
  @DisplayName("PATCH /api/jazigos/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200() throws Exception {
    JazigoUpdateDTO request = new JazigoUpdateDTO();
    request.setQuadra(2);
    request.setRua("B");
    request.setLote("20");

    var response = jazigoDTO(1L, 2, "B", "20");

    given(jazigoService.updateJazigo(eq(1L), any(JazigoUpdateDTO.class))).willReturn(response);

    mockMvc.perform(patch("/api/jazigos/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.quadra", is(2)))
        .andExpect(jsonPath("$.rua", is("B")))
        .andExpect(jsonPath("$.lote", is("20")));
  }

  @Test
  @DisplayName("PATCH /api/jazigos/{id} retorna 404 quando jazigo não existe")
  void testUpdateRetorna404() throws Exception {
    JazigoUpdateDTO request = new JazigoUpdateDTO();
    request.setQuadra(2);

    given(jazigoService.updateJazigo(eq(999L), any(JazigoUpdateDTO.class)))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Jazigo não encontrado com id 999"));

    mockMvc.perform(patch("/api/jazigos/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ===== DELETE =====
  @Test
  @DisplayName("DELETE /api/jazigos/{id} retorna 204 quando exclui com sucesso")
  void testDeleteRetorna204() throws Exception {
    doNothing().when(jazigoService).deleteJazigo(1L);

    mockMvc.perform(delete("/api/jazigos/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/jazigos/{id} retorna 404 quando não encontrado")
  void testDeleteRetorna404() throws Exception {
    doThrow(new com.sigesi.sigesi.config.NotFoundException("Jazigo não encontrado com id 999"))
        .when(jazigoService).deleteJazigo(999L);

    mockMvc.perform(delete("/api/jazigos/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
