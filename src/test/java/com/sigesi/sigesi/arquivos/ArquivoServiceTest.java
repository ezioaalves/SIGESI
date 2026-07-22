package com.sigesi.sigesi.arquivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.arquivos.dtos.FileUrlResponseDTO;
import com.sigesi.sigesi.config.NotFoundException;
import com.sigesi.sigesi.storage.MinioService;

/**
 * Testes unitarios para ArquivoService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArquivoService Tests")
class ArquivoServiceTest {

  @Mock
  private ArquivoRepository arquivoRepository;

  @Mock
  private ArquivoMapper arquivoMapper;

  @Mock
  private MinioService minioService;

  @InjectMocks
  private ArquivoService arquivoService;

  private Arquivo arquivo;
  private ArquivoResponseDTO responseDTO;

  @BeforeEach
  void setUp() {
    arquivo = Arquivo.builder()
        .id(1L).nomeOriginal("test.pdf").storageKey("uploads/test.pdf")
        .contentType("application/pdf").tamanho(1024L).ativo(true)
        .build();
    responseDTO = ArquivoResponseDTO.builder()
        .id(1L).nomeOriginal("test.pdf")
        .build();
  }

  @Test
  @DisplayName("Deve fazer upload de arquivo com sucesso")
  void testUploadFileSuccess() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf", "Test content".getBytes());

    when(minioService.uploadFile(any(), anyString())).thenReturn("storage-key");
    when(arquivoRepository.save(any(Arquivo.class))).thenReturn(arquivo);
    when(arquivoMapper.toDto(any(Arquivo.class))).thenReturn(responseDTO);

    ArquivoResponseDTO result = arquivoService.uploadFile(file, "test");

    assertNotNull(result);
    assertEquals("test.pdf", result.getNomeOriginal());
    verify(minioService, times(1)).uploadFile(any(), anyString());
    verify(arquivoRepository, times(1)).save(any(Arquivo.class));
  }

  @Test
  @DisplayName("Deve lancar excecao quando MinIO upload falha")
  void testUploadFileMinioFails() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf", "content".getBytes());

    when(minioService.uploadFile(any(), anyString()))
        .thenThrow(new RuntimeException("MinIO error"));

    assertThrows(RuntimeException.class,
        () -> arquivoService.uploadFile(file, "test"));
    verify(arquivoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve fazer rollback no MinIO quando salvamento no DB falha")
  void testUploadFileDbSaveFails() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf", "content".getBytes());

    when(minioService.uploadFile(any(), anyString())).thenReturn("key");
    when(arquivoRepository.save(any(Arquivo.class)))
        .thenThrow(new RuntimeException("DB error"));

    assertThrows(RuntimeException.class,
        () -> arquivoService.uploadFile(file, "test"));
    verify(minioService, times(1)).deleteFile(anyString());
  }

  @Test
  @DisplayName("Deve retornar metadados do arquivo por ID")
  void testGetFileMetadataComSucesso() {
    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));
    when(arquivoMapper.toDto(arquivo)).thenReturn(responseDTO);

    ArquivoResponseDTO result = arquivoService.getFileMetadata(1L);

    assertNotNull(result);
    assertEquals("test.pdf", result.getNomeOriginal());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para metadados inexistentes")
  void testGetFileMetadataNaoEncontrado() {
    when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> arquivoService.getFileMetadata(999L));
  }

  @Test
  @DisplayName("Deve retornar lista de arquivos")
  void testGetAllFiles() {
    when(arquivoRepository.findByAtivoTrueOrderByUploadedAtDesc())
        .thenReturn(List.of(arquivo));
    when(arquivoMapper.toDtoList(any())).thenReturn(List.of(responseDTO));

    List<ArquivoResponseDTO> result = arquivoService.getAllFiles();

    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Deve retornar lista vazia de arquivos")
  void testGetAllFilesVazio() {
    when(arquivoRepository.findByAtivoTrueOrderByUploadedAtDesc())
        .thenReturn(List.of());
    when(arquivoMapper.toDtoList(any())).thenReturn(List.of());

    List<ArquivoResponseDTO> result = arquivoService.getAllFiles();

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  @DisplayName("Deve gerar URL de download com sucesso")
  void testGenerateDownloadUrlComSucesso() {
    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));
    when(minioService.getPresignedUrl("uploads/test.pdf", 60))
        .thenReturn("https://minio/url");

    FileUrlResponseDTO result = arquivoService.generateDownloadUrl(1L);

    assertNotNull(result);
    assertEquals("https://minio/url", result.getUrl());
    assertEquals(1L, result.getFileId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para download URL inexistente")
  void testGenerateDownloadUrlNaoEncontrado() {
    when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> arquivoService.generateDownloadUrl(999L));
  }

  @Test
  @DisplayName("Deve fazer download de arquivo com sucesso")
  void testDownloadFileComSucesso() {
    InputStream mockStream = InputStream.nullInputStream();

    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));
    when(minioService.downloadFile("uploads/test.pdf")).thenReturn(mockStream);

    InputStream result = arquivoService.downloadFile(1L);

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para download inexistente")
  void testDownloadFileNaoEncontrado() {
    when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> arquivoService.downloadFile(999L));
  }

  @Test
  @DisplayName("Deve deletar arquivo com sucesso")
  void testDeleteFileComSucesso() {
    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));

    arquivoService.deleteFile(1L);

    verify(minioService, times(1)).deleteFile("uploads/test.pdf");
    verify(arquivoRepository, times(1)).delete(arquivo);
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para delecao inexistente")
  void testDeleteFileNaoEncontrado() {
    when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> arquivoService.deleteFile(999L));
  }

  @Test
  @DisplayName("Deve continuar delecao no DB quando MinIO falha")
  void testDeleteFileMinioFails() {
    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));
    doThrow(new RuntimeException("MinIO error")).when(minioService).deleteFile(anyString());

    arquivoService.deleteFile(1L);

    verify(arquivoRepository, times(1)).delete(arquivo);
  }

  @Test
  @DisplayName("Deve retornar entidade Arquivo por ID")
  void testGetArquivoEntityByIdComSucesso() {
    when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));

    Arquivo result = arquivoService.getArquivoEntityById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  @DisplayName("Deve lancar NotFoundException para entidade inexistente")
  void testGetArquivoEntityByIdNaoEncontrado() {
    when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> arquivoService.getArquivoEntityById(999L));
  }
}
