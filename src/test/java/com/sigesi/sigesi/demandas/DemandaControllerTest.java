package com.sigesi.sigesi.demandas;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.sigesi.sigesi.demandas.dtos.DemandaCreateDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaResponseDTO;
import com.sigesi.sigesi.demandas.dtos.DemandaUpdateDTO;
import com.sigesi.sigesi.usuarios.Usuario;

/**
 * Testes para DemandaController.
 */
@WebMvcTest(controllers = DemandaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DemandaController Tests")
class DemandaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private DemandaService service;

  private DemandaResponseDTO responseDto(Long id, DemandaStatus status) {
    Usuario responsavel = Usuario.builder().id(1L).email("agente@test.com").build();

    return DemandaResponseDTO.builder()
        .id(id)
        .solicitacao(null)
        .responsavel(responsavel)
        .prazo(LocalDate.of(2025, 12, 31))
        .status(status)
        .materiais(new HashSet<>())
        .build();
  }

  @Test
  @DisplayName("GET /api/demandas/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(service.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/demandas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/demandas/ retorna 200 com multiplas demandas")
  void testListAllRetorna200ComMultiplasDemandas() throws Exception {
    DemandaResponseDTO dto1 = responseDto(1L, DemandaStatus.PENDENTE);
    DemandaResponseDTO dto2 = responseDto(2L, DemandaStatus.EM_ANDAMENTO);

    given(service.getAll()).willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/demandas/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].status", is("PENDENTE")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  @DisplayName("GET /api/demandas/{id} retorna 200 quando encontrada")
  void testGetByIdRetorna200QuandoEncontrada() throws Exception {
    DemandaResponseDTO dto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.getDemandaById(1L)).willReturn(dto);

    mockMvc.perform(get("/api/demandas/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.status", is("PENDENTE")));
  }

  @Test
  @DisplayName("GET /api/demandas/{id} retorna 404 quando nao encontrada")
  void testGetByIdRetorna404QuandoNaoEncontrada() throws Exception {
    given(service.getDemandaById(999L))
        .willThrow(new NotFoundException("Demanda não encontrada com id 999"));

    mockMvc.perform(get("/api/demandas/999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/demandas/solicitacao/{id} retorna demandas por solicitacao")
  void testGetBySolicitacaoRetorna200() throws Exception {
    DemandaResponseDTO dto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.getDemandasBySolicitacao(1L)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/demandas/solicitacao/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("POST /api/demandas/ retorna 201 quando criada com sucesso")
  void testCreateRetorna201QuandoCriadaComSucesso() throws Exception {
    DemandaCreateDTO createDto = new DemandaCreateDTO();
    createDto.setSolicitacaoId(1L);
    createDto.setResponsavelId(1L);
    createDto.setPrazo(LocalDate.of(2025, 12, 31));
    createDto.setMateriaisIds(Set.of(1L, 2L));

    DemandaResponseDTO responseDto = responseDto(1L, DemandaStatus.PENDENTE);

    given(service.createDemanda(any(DemandaCreateDTO.class))).willReturn(responseDto);

    mockMvc.perform(post("/api/demandas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  @DisplayName("POST /api/demandas/ retorna 400 quando solicitacaoId nulo")
  void testCreateRetorna400QuandoSolicitacaoIdNulo() throws Exception {
    DemandaCreateDTO createDto = new DemandaCreateDTO();
    createDto.setSolicitacaoId(null);
    createDto.setPrazo(LocalDate.of(2025, 12, 31));

    mockMvc.perform(post("/api/demandas/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/demandas/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDadosAtualizados() throws Exception {
    DemandaUpdateDTO updateDto = new DemandaUpdateDTO();
    updateDto.setStatus(DemandaStatus.EM_ANDAMENTO);

    DemandaResponseDTO responseDto = responseDto(1L, DemandaStatus.EM_ANDAMENTO);

    given(service.updateDemanda(eq(1L), any(DemandaUpdateDTO.class)))
        .willReturn(responseDto);

    mockMvc.perform(patch("/api/demandas/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("EM_ANDAMENTO")));
  }

  @Test
  @DisplayName("DELETE /api/demandas/{id} retorna 204 quando deletada")
  void testDeleteRetorna204QuandoDeletada() throws Exception {
    doNothing().when(service).deleteDemanda(1L);

    mockMvc.perform(delete("/api/demandas/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/demandas/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException("Demanda não encontrada com id 999"))
        .when(service).deleteDemanda(999L);

    mockMvc.perform(delete("/api/demandas/999"))
        .andExpect(status().isNotFound());
  }
}
