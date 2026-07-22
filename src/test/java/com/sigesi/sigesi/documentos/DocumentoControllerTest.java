package com.sigesi.sigesi.documentos;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.documentos.dtos.DocumentoCreateDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoResponseDTO;
import com.sigesi.sigesi.documentos.dtos.DocumentoUpdateDTO;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DocumentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DocumentoController Tests")
class DocumentoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private DocumentoService documentoService;

  @MockitoBean
  private DocumentoPdfService documentoPdfService;

  private DocumentoResponseDTO documentoDTO(Long id, String subject, String body) {
    return DocumentoResponseDTO.builder()
        .id(id)
        .subject(subject)
        .body(body)
        .tipo(DocumentoTipo.OFICIO)
        .build();
  }

  private DocumentoCreateDTO createDTO(String subject, String body) {
    DocumentoCreateDTO dto = new DocumentoCreateDTO();
    dto.setSubject(subject);
    dto.setBody(body);
    dto.setTipo(DocumentoTipo.OFICIO);
    dto.setAssinante("João Silva");
    dto.setInteressado("Maria Santos");
    return dto;
  }

  @Test
  @DisplayName("GET /api/documentos/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(documentoService.getAll()).willReturn(List.of());

    mockMvc.perform(get("/api/documentos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  @DisplayName("GET /api/documentos/ retorna 200 com multiplos documentos")
  void testListAllRetorna200ComDocumentos() throws Exception {
    var doc1 = documentoDTO(1L, "Subject 1", "Body 1");
    var doc2 = documentoDTO(2L, "Subject 2", "Body 2");
    given(documentoService.getAll()).willReturn(List.of(doc1, doc2));

    mockMvc.perform(get("/api/documentos/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].subject", is("Subject 1")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].subject", is("Subject 2")));
  }

  @Test
  @DisplayName("GET /api/documentos/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200ComDocumento() throws Exception {
    var doc1 = documentoDTO(1L, "Test Subject", "Test Body");
    given(documentoService.getDocumentoById(1L)).willReturn(doc1);

    mockMvc.perform(get("/api/documentos/{id}", 1L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.subject", is("Test Subject")))
        .andExpect(jsonPath("$.body", is("Test Body")));
  }

  @Test
  @DisplayName("GET /api/documentos/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(documentoService.getDocumentoById(999L))
        .willThrow(new NotFoundException("Documento nao encontrado com id 999"));

    mockMvc.perform(get("/api/documentos/{id}", 999L).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/documentos/ retorna 201 quando criado com sucesso")
  void testCreateRetorna201ComDocumentoCriado() throws Exception {
    var createDTO = createDTO("New Subject", "New Body");
    var responseDTO = documentoDTO(1L, "New Subject", "New Body");
    given(documentoService.createDocumento(any(DocumentoCreateDTO.class))).willReturn(responseDTO);

    mockMvc.perform(post("/api/documentos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.subject", is("New Subject")));
  }

  @Test
  @DisplayName("POST /api/documentos/ retorna 400 quando dados invalidos")
  void testCreateRetorna400QuandoDadosInvalidos() throws Exception {
    DocumentoCreateDTO invalidDTO = new DocumentoCreateDTO();
    invalidDTO.setSubject("");
    invalidDTO.setBody("Body");
    invalidDTO.setAssinante("João Silva");
    invalidDTO.setInteressado("Maria Santos");

    mockMvc.perform(post("/api/documentos/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/documentos/{id} retorna 200 com dados atualizados")
  void testUpdateRetorna200ComDocumentoAtualizado() throws Exception {
    DocumentoUpdateDTO updateDTO = new DocumentoUpdateDTO();
    updateDTO.setSubject("Updated Subject");

    var updatedDTO = documentoDTO(1L, "Updated Subject", "Test Body");
    given(documentoService.updateDocumento(eq(1L), any(DocumentoUpdateDTO.class)))
        .willReturn(updatedDTO);

    mockMvc.perform(patch("/api/documentos/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.subject", is("Updated Subject")));
  }

  @Test
  @DisplayName("PATCH /api/documentos/{id} retorna 404 quando nao existe")
  void testUpdateRetorna404QuandoNaoEncontrado() throws Exception {
    DocumentoUpdateDTO updateDTO = new DocumentoUpdateDTO();
    updateDTO.setSubject("Updated Subject");

    given(documentoService.updateDocumento(eq(999L), any(DocumentoUpdateDTO.class)))
        .willThrow(new NotFoundException("Documento nao encontrado com id 999"));

    mockMvc.perform(patch("/api/documentos/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/documentos/{id} retorna 204 quando deletado com sucesso")
  void testDeleteRetorna204QuandoSucesso() throws Exception {
    doNothing().when(documentoService).deleteDocumento(1L);

    mockMvc.perform(delete("/api/documentos/{id}", 1L))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/documentos/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoEncontrado() throws Exception {
    doThrow(new NotFoundException("Documento nao encontrado com id 999"))
        .when(documentoService).deleteDocumento(999L);

    mockMvc.perform(delete("/api/documentos/{id}", 999L))
        .andExpect(status().isNotFound());
  }

}
