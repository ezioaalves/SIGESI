package com.sigesi.sigesi.pessoas;

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
import com.sigesi.sigesi.pessoas.dtos.PessoaCreateDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaResponseDTO;
import com.sigesi.sigesi.pessoas.dtos.PessoaUpdateDTO;

@WebMvcTest(controllers = PessoaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PessoaController Tests")
class PessoaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PessoaService pessoaService;

  // ===== Métodos auxiliares =====
  private PessoaResponseDTO pessoaDTO(Long id, String nome, String cpf, SexoEnum sexo) {
    PessoaResponseDTO dto = new PessoaResponseDTO();
    dto.setId(id);
    dto.setNome(nome);
    dto.setCpf(cpf);
    dto.setSexo(sexo.name());
    return dto;
  }

  private PessoaCreateDTO createDTO(String nome, String cpf, SexoEnum sexo, Long enderecoId) {
    PessoaCreateDTO dto = new PessoaCreateDTO();
    dto.setNome(nome);
    dto.setCpf(cpf);
    dto.setSexo(sexo);
    dto.setEnderecoId(enderecoId);
    return dto;
  }

  // ===== GET =====
  @Test
  @DisplayName("GET /api/pessoas/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(pessoaService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/pessoas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/pessoas/ retorna 200 com pessoas")
  void testListAllRetorna200ComPessoas() throws Exception {
    var p1 = pessoaDTO(1L, "João Silva", "123.456.789-00", SexoEnum.MASCULINO);
    var p2 = pessoaDTO(2L, "Maria Santos", "987.654.321-00", SexoEnum.FEMININO);
    given(pessoaService.getAll()).willReturn(List.of(p1, p2));

    mockMvc.perform(get("/api/pessoas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nome", is("João Silva")))
        .andExpect(jsonPath("$[0].cpf", is("123.456.789-00")))
        .andExpect(jsonPath("$[0].sexo", is("MASCULINO")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].nome", is("Maria Santos")));
  }

  @Test
  @DisplayName("GET /api/pessoas/{id} retorna 200 com pessoa encontrada")
  void testGetByIdRetorna200ComPessoa() throws Exception {
    var p1 = pessoaDTO(1L, "João Silva", "123.456.789-00", SexoEnum.MASCULINO);
    given(pessoaService.getPessoaById(1L)).willReturn(p1);

    mockMvc.perform(get("/api/pessoas/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("João Silva")))
        .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
        .andExpect(jsonPath("$.sexo", is("MASCULINO")));
  }

  @Test
  @DisplayName("GET /api/pessoas/{id} retorna 404 quando não encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(pessoaService.getPessoaById(999L))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Pessoa não encontrada com id 999"));

    mockMvc.perform(get("/api/pessoas/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/pessoas/cpf retorna 200 com pessoa encontrada por CPF")
  void testGetByCpfRetorna200ComPessoa() throws Exception {
    var p1 = pessoaDTO(1L, "João Silva", "123.456.789-00", SexoEnum.MASCULINO);
    given(pessoaService.getPessoaByCpf("123.456.789-00")).willReturn(p1);

    mockMvc.perform(get("/api/pessoas/cpf")
        .param("cpf", "123.456.789-00")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
        .andExpect(jsonPath("$.nome", is("João Silva")));
  }

  // ===== POST =====
  @Test
  @DisplayName("POST /api/pessoas/ retorna 201 ao criar com sucesso")
  void testCreateRetorna201() throws Exception {
    var request = createDTO("João Silva", "123.456.789-00", SexoEnum.MASCULINO, 1L);
    var response = pessoaDTO(1L, "João Silva", "123.456.789-00", SexoEnum.MASCULINO);

    given(pessoaService.createPessoa(any(PessoaCreateDTO.class))).willReturn(response);

    mockMvc.perform(post("/api/pessoas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("João Silva")))
        .andExpect(jsonPath("$.cpf", is("123.456.789-00")))
        .andExpect(jsonPath("$.sexo", is("MASCULINO")));
  }

  @Test
  @DisplayName("POST /api/pessoas/ retorna 400 quando dados inválidos")
  void testCreateRetorna400() throws Exception {
    var request = createDTO("", null, null, null);

    mockMvc.perform(post("/api/pessoas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== PATCH =====
  @Test
  @DisplayName("PATCH /api/pessoas/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200() throws Exception {
    PessoaUpdateDTO request = new PessoaUpdateDTO();
    request.setNome("João Silva Atualizado");

    var response = pessoaDTO(1L, "João Silva Atualizado", "123.456.789-00", SexoEnum.MASCULINO);

    given(pessoaService.updatePessoa(eq(1L), any(PessoaUpdateDTO.class))).willReturn(response);

    mockMvc.perform(patch("/api/pessoas/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nome", is("João Silva Atualizado")));
  }

  @Test
  @DisplayName("PATCH /api/pessoas/{id} retorna 404 quando pessoa não existe")
  void testUpdateRetorna404() throws Exception {
    PessoaUpdateDTO request = new PessoaUpdateDTO();
    request.setNome("Novo Nome");

    given(pessoaService.updatePessoa(eq(999L), any(PessoaUpdateDTO.class)))
        .willThrow(new com.sigesi.sigesi.config.NotFoundException("Pessoa não encontrada com id 999"));

    mockMvc.perform(patch("/api/pessoas/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ===== DELETE =====
  @Test
  @DisplayName("DELETE /api/pessoas/{id} retorna 204 quando exclui com sucesso")
  void testDeleteRetorna204() throws Exception {
    doNothing().when(pessoaService).deletePessoa(1L);

    mockMvc.perform(delete("/api/pessoas/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/pessoas/{id} retorna 404 quando não encontrado")
  void testDeleteRetorna404() throws Exception {
    doThrow(new com.sigesi.sigesi.config.NotFoundException("Pessoa não encontrada com id 999"))
        .when(pessoaService).deletePessoa(999L);

    mockMvc.perform(delete("/api/pessoas/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
