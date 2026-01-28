package com.sigesi.sigesi.comentarios;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import com.sigesi.sigesi.comentarios.dtos.ComentarioCreateDTO;
import com.sigesi.sigesi.comentarios.dtos.ComentarioResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.usuarios.Usuario;

/**
 * Testes para ComentarioController.
 */
@WebMvcTest(controllers = ComentarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ComentarioController Tests")
class ComentarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ComentarioService service;

  private ComentarioResponseDTO responseDto(Long id, String texto) {
    Usuario autor = Usuario.builder().id(1L).email("autor@test.com").build();

    return ComentarioResponseDTO.builder()
        .id(id)
        .demandaId(1L)
        .autor(autor)
        .texto(texto)
        .criadoEm(LocalDateTime.of(2025, 12, 29, 10, 0))
        .build();
  }

  @Test
  @DisplayName("GET /api/comentarios/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/comentarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/comentarios/ retorna 200 com multiplos comentarios")
  void testListAllRetorna200ComMultiplosComentarios() throws Exception {
    ComentarioResponseDTO dto1 = responseDto(1L, "Comentario 1");
    ComentarioResponseDTO dto2 = responseDto(2L, "Comentario 2");

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/comentarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].texto", is("Comentario 1")));
  }

  @Test
  @DisplayName("GET /api/comentarios/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200QuandoEncontrado() throws Exception {
    ComentarioResponseDTO dto = responseDto(1L, "Test");

    given(service.getComentarioById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/comentarios/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.texto", is("Test")));
  }

  @Test
  @DisplayName("GET /api/comentarios/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(service.getComentarioById(999L))
        .willThrow(new NotFoundException("Comentário não encontrado com id 999"));

    mockMvc.perform(get("/api/comentarios/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/comentarios/demanda/{id} retorna comentarios por demanda")
  void testGetByDemandaRetorna200() throws Exception {
    ComentarioResponseDTO dto = responseDto(1L, "Test");

    given(service.getComentariosByDemanda(1L)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/comentarios/demanda/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/comentarios/ retorna 201 quando criado com sucesso")
  void testCreateRetorna201QuandoCriadoComSucesso() throws Exception {
    ComentarioCreateDTO createDto = new ComentarioCreateDTO(1L, 1L, "Novo comentario");
    ComentarioResponseDTO responseDto = responseDto(1L, "Novo comentario");

    given(service.createComentario(any(ComentarioCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/comentarios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  @DisplayName("POST /api/comentarios/ retorna 400 quando texto vazio")
  void testCreateRetorna400QuandoTextoVazio() throws Exception {
    ComentarioCreateDTO createDto = new ComentarioCreateDTO(1L, 1L, "");

    mockMvc.perform(post("/api/comentarios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("DELETE /api/comentarios/{id} retorna 204 quando deletado")
  void testDeleteRetorna204QuandoDeletado() throws Exception {
    doNothing().when(service).deleteComentario(1L);

    mockMvc.perform(delete("/api/comentarios/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/comentarios/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Comentário não encontrado com id 999"))
        .when(service).deleteComentario(999L);

    mockMvc.perform(delete("/api/comentarios/999"))
        .andExpect(status().isNotFound());
  }
}
