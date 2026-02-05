package com.sigesi.sigesi.arquivos;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;

/**
 * Testes para ArquivoController.
 */
@WebMvcTest(controllers = ArquivoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ArquivoController Tests")
class ArquivoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ArquivoService arquivoService;

  private ArquivoResponseDTO responseDto(Long id, String nome) {
    return ArquivoResponseDTO.builder()
        .id(id)
        .nomeOriginal(nome)
        .contentType("application/pdf")
        .tamanho(12L)
        .ativo(true)
        .build();
  }

  @Test
  @DisplayName("POST /api/arquivos/upload retorna 201 quando upload com sucesso")
  void testUploadFileRetorna201() throws Exception {
    ArquivoResponseDTO response = responseDto(1L, "test.pdf");

    given(arquivoService.uploadFile(any(), any())).willReturn(response);

    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf",
        "Test content".getBytes());

    mockMvc.perform(multipart("/api/arquivos/upload")
        .file(file)
        .param("categoria", "test"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nomeOriginal", is("test.pdf")))
        .andExpect(jsonPath("$.contentType", is("application/pdf")));
  }

  @Test
  @DisplayName("GET /api/arquivos/ retorna 200 com lista vazia")
  void testListAllRetorna200ComListaVazia() throws Exception {
    given(arquivoService.getAllFiles()).willReturn(List.of());

    mockMvc.perform(get("/api/arquivos/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/arquivos/ retorna 200 com multiplos arquivos")
  void testListAllRetorna200ComMultiplosArquivos() throws Exception {
    ArquivoResponseDTO dto1 = responseDto(1L, "file1.pdf");
    ArquivoResponseDTO dto2 = responseDto(2L, "file2.pdf");

    given(arquivoService.getAllFiles())
        .willReturn(List.of(dto1, dto2));

    mockMvc.perform(get("/api/arquivos/")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].nomeOriginal", is("file1.pdf")))
        .andExpect(jsonPath("$[1].id", is(2)));
  }

  @Test
  @DisplayName("GET /api/arquivos/{id} retorna 200 quando encontrado")
  void testGetByIdRetorna200QuandoEncontrado() throws Exception {
    ArquivoResponseDTO dto = responseDto(1L, "test.pdf");

    given(arquivoService.getFileMetadata(1L)).willReturn(dto);

    mockMvc.perform(get("/api/arquivos/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.nomeOriginal", is("test.pdf")));
  }

  @Test
  @DisplayName("GET /api/arquivos/{id} retorna 404 quando nao encontrado")
  void testGetByIdRetorna404QuandoNaoEncontrado() throws Exception {
    given(arquivoService.getFileMetadata(999L))
        .willThrow(new NotFoundException(
            "Arquivo não encontrado com id 999"));

    mockMvc.perform(get("/api/arquivos/999")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/arquivos/{id} retorna 204 quando deletado")
  void testDeleteRetorna204QuandoDeletado() throws Exception {
    doNothing().when(arquivoService).deleteFile(1L);

    mockMvc.perform(delete("/api/arquivos/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/arquivos/{id} retorna 404 quando nao existe")
  void testDeleteRetorna404QuandoNaoExiste() throws Exception {
    doThrow(new NotFoundException(
        "Arquivo não encontrado com id 999"))
        .when(arquivoService).deleteFile(999L);

    mockMvc.perform(delete("/api/arquivos/999"))
        .andExpect(status().isNotFound());
  }
}
