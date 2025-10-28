package com.sigesi.sigesi.cemiterios;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.sigesi.sigesi.enderecos.Endereco;

@WebMvcTest(controllers = CemiterioController.class)
@AutoConfigureMockMvc(addFilters = false) // desabilita filtros de segurança para simplificar os testes
@DisplayName("CemiterioController Tests")
class CemiterioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CemiterioService cemiterioService;

  private Endereco endereco(Long id) {
    return Endereco.builder()
        .id(id)
        .logradouro("Rua Exemplo")
        .numero("123")
        .bairro("Centro")
        .build();
  }

  private Cemiterio cemiterio(Long id, String nome, Long enderecoId) {
    return Cemiterio.builder()
        .id(id)
        .nome(nome)
        .endereco(endereco(enderecoId))
        .build();
  }

  @Test
  @DisplayName("GET /api/cemiterios/ deve retornar 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(cemiterioService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/cemiterios/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/cemiterios/ deve retornar 200 com lista de cemitérios")
  void testListAllRetorna200ComCemiterios() throws Exception {
    var c1 = cemiterio(1L, "Cemitério Central", 1L);
    var c2 = cemiterio(2L, "Cemitério Municipal", 1L);
    given(cemiterioService.getAll()).willReturn(List.of(c1, c2));

    mockMvc.perform(get("/api/cemiterios/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nome", is("Cemitério Central")))
        .andExpect(jsonPath("$[0].endereco.id", is(1)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].nome", is("Cemitério Municipal")));
  }

  @Test
  @DisplayName("GET /api/cemiterios/{id} deve retornar 200 com cemitério encontrado")
  void testGetByIdRetorna200ComCemiterio() throws Exception {
    var c1 = cemiterio(1L, "Cemitério Central", 1L);
    given(cemiterioService.getCemiterioById(1L)).willReturn(c1);

    mockMvc.perform(get("/api/cemiterios/{id}", 1L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cemitério Central")))
        .andExpect(jsonPath("$.endereco.id", is(1)));
  }

  @Test
  @DisplayName("GET /api/cemiterios/{id} deve retornar 500 quando não encontrado")
  void testGetByIdRetorna500QuandoNaoEncontrado() throws Exception {
    given(cemiterioService.getCemiterioById(999L))
        .willThrow(new RuntimeException("Cemitério não encontrado com id 999"));

    mockMvc.perform(get("/api/cemiterios/{id}", 999L)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()); // sem error handler global
  }

  @Test
  @DisplayName("POST /api/cemiterios/ deve retornar 201 ao criar com sucesso")
  void testCreateRetorna201ComCemiterioValido() throws Exception {
    var request = Cemiterio.builder()
        .nome("Cemitério Central")
        .endereco(Endereco.builder().id(1L).build())
        .build();

    var created = cemiterio(1L, "Cemitério Central", 1L);
    given(cemiterioService.createCemiterio(any(Cemiterio.class))).willReturn(created);

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Cemitério Central")));
  }

  @Test
  @DisplayName("POST /api/cemiterios/ deve retornar 400 quando nome é omitido")
  void testCreateRetorna400SemNome() throws Exception {
    String body = "{\n" +
        "  \"endereco\": { \"id\": 1 }\n" +
        "}";

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/cemiterios/ deve retornar 400 quando nome é vazio")
  void testCreateRetorna400ComNomeVazio() throws Exception {
    String body = "{\n" +
        "  \"nome\": \"\",\n" +
        "  \"endereco\": { \"id\": 1 }\n" +
        "}";

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/cemiterios/ deve retornar 400 quando nome é apenas espaços")
  void testCreateRetorna400ComNomeApenasEspacos() throws Exception {
    String body = "{\n" +
        "  \"nome\": \"   \",\n" +
        "  \"endereco\": { \"id\": 1 }\n" +
        "}";

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/cemiterios/ deve retornar 400 quando endereço é omitido")
  void testCreateRetorna400SemEndereco() throws Exception {
    String body = "{\n" +
        "  \"nome\": \"Cemitério Central\"\n" +
        "}";

    mockMvc.perform(post("/api/cemiterios/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PUT /api/cemiterios/{id} deve retornar 200 com dados atualizados")
  void testUpdateRetorna200ComCemiterioAtualizado() throws Exception {
    var request = Cemiterio.builder()
        .nome("Novo Nome")
        .endereco(Endereco.builder().id(2L).build())
        .build();

    var atualizado = cemiterio(1L, "Novo Nome", 2L);
    given(cemiterioService.updateCemiterio(eq(1L), any(Cemiterio.class))).willReturn(atualizado);

    mockMvc.perform(put("/api/cemiterios/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("Novo Nome")))
        .andExpect(jsonPath("$.endereco.id", is(2)));
  }

  @Test
  @DisplayName("PUT /api/cemiterios/{id} deve retornar 400 com dados inválidos")
  void testUpdateRetorna400ComDadosInvalidos() throws Exception {
    String body = "{\n" +
        "  \"nome\": \"\",\n" +
        "  \"endereco\": { \"id\": 1 }\n" +
        "}";

    mockMvc.perform(put("/api/cemiterios/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PUT /api/cemiterios/{id} deve retornar 500 quando cemitério não existe")
  void testUpdateRetorna500QuandoNaoEncontrado() throws Exception {
    var request = Cemiterio.builder()
        .nome("Novo Nome")
        .endereco(Endereco.builder().id(2L).build())
        .build();

    given(cemiterioService.updateCemiterio(eq(999L), any(Cemiterio.class)))
        .willThrow(new RuntimeException("Cemitério não encontrado com id 999"));

    mockMvc.perform(put("/api/cemiterios/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("DELETE /api/cemiterios/{id} deve retornar 204 quando excluir com sucesso")
  void testDeleteRetorna204QuandoExcluiComSucesso() throws Exception {
    doNothing().when(cemiterioService).deleteCemiterio(1L);

    mockMvc.perform(delete("/api/cemiterios/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/cemiterios/{id} deve retornar 500 quando não encontrado")
  void testDeleteRetorna500QuandoNaoEncontrado() throws Exception {
    doThrow(new RuntimeException("Cemitério não encontrado com id 999"))
        .when(cemiterioService).deleteCemiterio(999L);

    mockMvc.perform(delete("/api/cemiterios/{id}", 999L))
        .andExpect(status().isInternalServerError());
  }
}
