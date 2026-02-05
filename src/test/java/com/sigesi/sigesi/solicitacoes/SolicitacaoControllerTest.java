package com.sigesi.sigesi.solicitacoes;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.authentication.CustomOAuth2User;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.enderecos.Endereco;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoCreateDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoResponseDTO;
import com.sigesi.sigesi.solicitacoes.dtos.SolicitacaoUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;
import com.sigesi.sigesi.usuarios.enums.Role;

/**
 * Testes para SolicitacaoController.
 */
@WebMvcTest(controllers = SolicitacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SolicitacaoController Tests")
class SolicitacaoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private SolicitacaoService service;

  private org.springframework.security.core.Authentication mockAuth;

  @BeforeEach
  void setUp() {
    Usuario usuario = Usuario.builder()
        .id(1L)
        .email("test@test.com")
        .role(Role.ADMIN)
        .build();

    CustomOAuth2User mockOAuth2User = mock(CustomOAuth2User.class);
    when(mockOAuth2User.getUser()).thenReturn(usuario);

    mockAuth = mock(
        org.springframework.security.core.Authentication.class);
    when(mockAuth.getPrincipal()).thenReturn(mockOAuth2User);
  }

  private SolicitacaoResponseDTO responseDto(
      Long id, SolicitacaoAssunto assunto, String body) {
    Usuario autor = Usuario.builder()
        .id(1L).email("test@test.com").build();
    Endereco local = Endereco.builder()
        .id(1L).logradouro("Rua Test").build();

    return SolicitacaoResponseDTO.builder()
        .id(id)
        .data(LocalDate.of(2025, 12, 29))
        .assunto(assunto)
        .body(body)
        .anexos(List.of())
        .autor(autor)
        .local(local)
        .build();
  }

  private SolicitacaoCreateDTO createDto(
      SolicitacaoAssunto assunto, String body) {
    SolicitacaoCreateDTO dto = new SolicitacaoCreateDTO();
    dto.setAssunto(assunto);
    dto.setBody(body);
    dto.setAnexoIds(null);
    dto.setAutorId(1L);
    dto.setLocalId(1L);
    return dto;
  }

  @Test
  @DisplayName("GET /api/solicitacoes/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll(any(Usuario.class))).willReturn(List.of());

    mockMvc.perform(get("/api/solicitacoes/")
        .with(request -> {
          request.setUserPrincipal(mockAuth);
          return request;
        })
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(
            MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/solicitacoes/ retorna 200 com multiplas solicitacoes")
  void testListAllRetorna200ComMultiplasSolicitacoes() throws Exception {
    SolicitacaoResponseDTO dto1 = responseDto(1L, SolicitacaoAssunto.BURACO, "Corpo 1");
    SolicitacaoResponseDTO dto2 = responseDto(2L, SolicitacaoAssunto.ESGOTO, "Corpo 2");

    given(service.getAll(any(Usuario.class)))
        .willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/solicitacoes/")
        .with(request -> {
          request.setUserPrincipal(mockAuth);
          return request;
        })
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].assunto", is("BURACO")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].assunto", is("ESGOTO")));
  }

  @Test
  @DisplayName("GET /api/solicitacoes/{id} retorna 200 quando encontrada")
  void testGetByIdRetorna200QuandoEncontrada() throws Exception {
    SolicitacaoResponseDTO dto = responseDto(
        1L, SolicitacaoAssunto.ESGOTO, "Test Body");

    given(service.getSolicitacaoById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/solicitacoes/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.assunto", is("ESGOTO")))
        .andExpect(jsonPath("$.body", is("Test Body")));
  }

  @Test
  @DisplayName("GET /api/solicitacoes/{id} retorna 404 quando nao encontrada")
  void testGetByIdRetorna404QuandoNaoEncontrada() throws Exception {
    given(service.getSolicitacaoById(999L))
        .willThrow(new NotFoundException(
            "Solicitação não encontrada com id 999"));

    mockMvc.perform(get("/api/solicitacoes/999")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/solicitacoes/ retorna 201 quando criada")
  void testCreateRetorna201QuandoCriadaComSucesso() throws Exception {
    SolicitacaoCreateDTO createDto = createDto(
        SolicitacaoAssunto.BURACO, "Novo Corpo");
    SolicitacaoResponseDTO responseDto = responseDto(
        1L, SolicitacaoAssunto.BURACO, "Novo Corpo");

    given(service.createSolicitacao(any(SolicitacaoCreateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(post("/api/solicitacoes/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.assunto", is("BURACO")))
        .andExpect(jsonPath("$.body", is("Novo Corpo")));
  }

  @Test
  @DisplayName("POST /api/solicitacoes/ retorna 400 quando assunto vazio")
  void testCreateRetorna400QuandoAssuntoVazio() throws Exception {
    SolicitacaoCreateDTO createDto = createDto(null, "Corpo válido");

    mockMvc.perform(post("/api/solicitacoes/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/solicitacoes/ retorna 400 quando body vazio")
  void testCreateRetorna400QuandoBodyVazio() throws Exception {
    SolicitacaoCreateDTO createDto = createDto(
        SolicitacaoAssunto.BURACO, "");

    mockMvc.perform(post("/api/solicitacoes/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/solicitacoes/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDadosAtualizados() throws Exception {
    SolicitacaoUpdateDTO updateDto = new SolicitacaoUpdateDTO(
        SolicitacaoStatus.ABERTA);
    SolicitacaoResponseDTO responseDto = responseDto(
        1L, SolicitacaoAssunto.BURACO, "Corpo Atualizado");

    given(service.updateSolicitacao(eq(1L), any(SolicitacaoUpdateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(patch("/api/solicitacoes/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.assunto", is("BURACO")))
        .andExpect(jsonPath("$.body", is("Corpo Atualizado")));
  }

  @Test
  @DisplayName("PATCH /api/solicitacoes/{id} retorna 404 quando nao existe")
  void testUpdateRetorna404QuandoRecursoNaoExiste() throws Exception {
    SolicitacaoUpdateDTO updateDto = new SolicitacaoUpdateDTO(
        SolicitacaoStatus.ABERTA);

    given(service.updateSolicitacao(
        eq(999L), any(SolicitacaoUpdateDTO.class)))
        .willThrow(new NotFoundException(
            "Solicitação não encontrada com id 999"));

    mockMvc.perform(patch("/api/solicitacoes/999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/solicitacoes/{id} retorna 204 quando deletada")
  void testDeleteRetorna204QuandoDeletadaComSucesso() throws Exception {
    doNothing().when(service).deleteSolicitacao(1L);

    mockMvc.perform(delete("/api/solicitacoes/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/solicitacoes/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoRecursoNaoExiste() throws Exception {
    doThrow(new NotFoundException(
        "Solicitação não encontrada com id 999"))
        .when(service).deleteSolicitacao(999L);

    mockMvc.perform(delete("/api/solicitacoes/999"))
        .andExpect(status().isNotFound());
  }
}
