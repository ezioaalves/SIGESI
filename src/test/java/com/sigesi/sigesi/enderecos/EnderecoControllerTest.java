package com.sigesi.sigesi.enderecos;

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
import com.sigesi.sigesi.enderecos.dtos.EnderecoCreateDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoResponseDTO;
import com.sigesi.sigesi.enderecos.dtos.EnderecoUpdateDTO;

@WebMvcTest(controllers = EnderecoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EnderecoController Tests")
class EnderecoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EnderecoService enderecoService;

  // ===== Métodos auxiliares =====
  private EnderecoResponseDTO enderecoDTO(Long id, String logradouro, String numero, String bairro) {
    return EnderecoResponseDTO.builder()
        .id(id)
        .logradouro(logradouro)
        .numero(numero)
        .bairro(bairro)
        .build();
  }

  private EnderecoCreateDTO createDTO(String logradouro, String numero, String bairro) {
    return new EnderecoCreateDTO(logradouro, numero, bairro, null);
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/enderecos/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(enderecoService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/enderecos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/enderecos/ retorna 200 com endereços")
  void testListAllRetorna200ComEnderecos() throws Exception {
    var e1 = enderecoDTO(1L, "Rua A", "100", "Centro");
    var e2 = enderecoDTO(2L, "Avenida B", "200", "Zona Norte");
    given(enderecoService.getAll()).willReturn(List.of(e1, e2));

    mockMvc.perform(get("/api/enderecos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].logradouro", is("Rua A")))
        .andExpect(jsonPath("$[0].numero", is("100")))
        .andExpect(jsonPath("$[0].bairro", is("Centro")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].logradouro", is("Avenida B")));
  }

  @Test
  @DisplayName("GET /api/enderecos/{id} retorna 200 com endereço encontrado")
  void testGetByIdRetorna200ComEndereco() throws Exception {
    var e1 = enderecoDTO(1L, "Rua Exemplo", "123", "Centro");
    given(enderecoService.getEnderecoById(1L)).willReturn(e1);

    mockMvc.perform(get("/api/enderecos/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.logradouro", is("Rua Exemplo")))
        .andExpect(jsonPath("$.numero", is("123")))
        .andExpect(jsonPath("$.bairro", is("Centro")));
  }

  @Test
  @DisplayName("GET /api/enderecos/{id} retorna 404 quando não encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(enderecoService.getEnderecoById(999L))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Endereço não encontrado com id 999"));

    mockMvc.perform(get("/api/enderecos/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  // ===== POST =====
  @Test
  @DisplayName("POST /api/enderecos/ retorna 201 ao criar com sucesso")
  void testCreateRetorna201() throws Exception {
    var request = createDTO("Rua Nova", "456", "Bairro Novo");
    var response = enderecoDTO(1L, "Rua Nova", "456", "Bairro Novo");

    given(enderecoService.createEndereco(any(EnderecoCreateDTO.class))).willReturn(response);

    mockMvc.perform(post("/api/enderecos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.logradouro", is("Rua Nova")))
        .andExpect(jsonPath("$.numero", is("456")))
        .andExpect(jsonPath("$.bairro", is("Bairro Novo")));
  }

  @Test
  @DisplayName("POST /api/enderecos/ retorna 400 quando dados inválidos")
  void testCreateRetorna400() throws Exception {
    var request = createDTO("", null, "");

    mockMvc.perform(post("/api/enderecos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== PATCH =====
  @Test
  @DisplayName("PATCH /api/enderecos/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200() throws Exception {
    var request = new EnderecoUpdateDTO("Rua Atualizada", "789", "Bairro Atualizado", "Nova Ref");
    var response = enderecoDTO(1L, "Rua Atualizada", "789", "Bairro Atualizado");

    given(enderecoService.updateEndereco(eq(1L), any(EnderecoUpdateDTO.class))).willReturn(response);

    mockMvc.perform(patch("/api/enderecos/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.logradouro", is("Rua Atualizada")))
        .andExpect(jsonPath("$.numero", is("789")))
        .andExpect(jsonPath("$.bairro", is("Bairro Atualizado")));
  }

  @Test
  @DisplayName("PATCH /api/enderecos/{id} retorna 404 quando endereço não existe")
  void testUpdateRetorna404() throws Exception {
    var request = new EnderecoUpdateDTO("Rua Nova", "456", "Bairro Novo", "Referência");

    given(enderecoService.updateEndereco(eq(999L), any(EnderecoUpdateDTO.class)))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Endereço não encontrado com id 999"));

    mockMvc.perform(patch("/api/enderecos/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ===== DELETE =====
  @Test
  @DisplayName("DELETE /api/enderecos/{id} retorna 204 quando exclui com sucesso")
  void testDeleteRetorna204() throws Exception {
    doNothing().when(enderecoService).deleteEndereco(1L);

    mockMvc.perform(delete("/api/enderecos/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/enderecos/{id} retorna 404 quando não encontrado")
  void testDeleteRetorna404() throws Exception {
    doThrow(new com.sigesi.sigesi.config.NotFoundException("Endereço não encontrado com id 999"))
        .when(enderecoService).deleteEndereco(999L);

    mockMvc.perform(delete("/api/enderecos/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
