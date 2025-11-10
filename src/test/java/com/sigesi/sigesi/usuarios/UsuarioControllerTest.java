package com.sigesi.sigesi.usuarios;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
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

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UsuarioController Tests")
class UsuarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UsuarioService usuarioService;

  // ===== Métodos auxiliares =====
  private Usuario usuarioMock(Long id, String email, String name, Boolean ativo) {
    return Usuario.builder()
        .id(id)
        .email(email)
        .name(name)
        .pictureUrl("https://example.com/pic.jpg")
        .provider("google")
        .ativo(ativo)
        .build();
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/usuarios/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(usuarioService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/usuarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/usuarios/ retorna 200 com usuários")
  void testListAllRetorna200ComUsuarios() throws Exception {
    var u1 = usuarioMock(1L, "usuario1@example.com", "Usuário 1", true);
    var u2 = usuarioMock(2L, "usuario2@example.com", "Usuário 2", false);
    given(usuarioService.getAll()).willReturn(List.of(u1, u2));

    mockMvc.perform(get("/api/usuarios/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].email", is("usuario1@example.com")))
        .andExpect(jsonPath("$[0].name", is("Usuário 1")))
        .andExpect(jsonPath("$[0].ativo", is(true)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].ativo", is(false)));
  }

  @Test
  @DisplayName("GET /api/usuarios/{id} retorna 200 com usuário encontrado")
  void testGetByIdRetorna200ComUsuario() throws Exception {
    var u1 = usuarioMock(1L, "usuario@example.com", "João Silva", true);
    given(usuarioService.getUsuarioById(1L)).willReturn(u1);

    mockMvc.perform(get("/api/usuarios/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("usuario@example.com")))
        .andExpect(jsonPath("$.name", is("João Silva")))
        .andExpect(jsonPath("$.ativo", is(true)));
  }

  @Test
  @DisplayName("GET /api/usuarios/{id} retorna 500 quando não encontrado")
  void testGetByIdRetorna500QuandoNaoEncontrado() throws Exception {
    given(usuarioService.getUsuarioById(999L))
        .willThrow(new RuntimeException("Usuário não encontrado com id 999"));

    mockMvc.perform(get("/api/usuarios/{id}", 999L))
        .andExpect(status().isInternalServerError());
  }

  // ===== PATCH =====
  @Test
  @DisplayName("PATCH /api/usuarios/{id}/toggle-ativo retorna 200 com status alterado")
  void testToggleAtivoRetorna200() throws Exception {
    var usuarioAtivo = usuarioMock(1L, "usuario@example.com", "João Silva", true);
    var usuarioInativo = usuarioMock(1L, "usuario@example.com", "João Silva", false);

    given(usuarioService.toggleUsuarioAtivo(1L)).willReturn(usuarioInativo);

    mockMvc.perform(patch("/api/usuarios/{id}/toggle-ativo", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.ativo", is(false)));
  }

  @Test
  @DisplayName("PATCH /api/usuarios/{id}/toggle-ativo retorna 500 quando usuário não existe")
  void testToggleAtivoRetorna500QuandoUsuarioNaoExiste() throws Exception {
    given(usuarioService.toggleUsuarioAtivo(999L))
        .willThrow(new RuntimeException("Usuário não encontrado com id 999"));

    mockMvc.perform(patch("/api/usuarios/{id}/toggle-ativo", 999L))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("PATCH /api/usuarios/{id}/toggle-ativo alterna de inativo para ativo")
  void testToggleAtivoDeInativoParaAtivo() throws Exception {
    var usuarioInativo = usuarioMock(1L, "usuario@example.com", "João Silva", false);
    var usuarioAtivo = usuarioMock(1L, "usuario@example.com", "João Silva", true);

    given(usuarioService.toggleUsuarioAtivo(1L)).willReturn(usuarioAtivo);

    mockMvc.perform(patch("/api/usuarios/{id}/toggle-ativo", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ativo", is(true)));
  }
}
