package com.sigesi.sigesi.arquivos;

import com.sigesi.sigesi.arquivos.dtos.ArquivoResponseDTO;
import com.sigesi.sigesi.storage.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ArquivoService.
 */
class ArquivoServiceTest {

  @Mock
  private ArquivoRepository arquivoRepository;

  @Mock
  private ArquivoMapper arquivoMapper;

  @Mock
  private MinioService minioService;

  @InjectMocks
  private ArquivoService arquivoService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testUploadFileSuccess() {
    // Arrange
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.pdf",
        "application/pdf",
        "Test content".getBytes()
    );

    Arquivo arquivo = Arquivo.builder()
        .id(1L)
        .nomeOriginal("test.pdf")
        .contentType("application/pdf")
        .tamanho(12L)
        .build();

    ArquivoResponseDTO responseDTO = ArquivoResponseDTO.builder()
        .id(1L)
        .nomeOriginal("test.pdf")
        .build();

    when(minioService.uploadFile(any(), anyString())).thenReturn("storage-key");
    when(arquivoRepository.save(any(Arquivo.class))).thenReturn(arquivo);
    when(arquivoMapper.toDto(any(Arquivo.class))).thenReturn(responseDTO);

    // Act
    ArquivoResponseDTO result = arquivoService.uploadFile(file, "test");

    // Assert
    assertNotNull(result);
    assertEquals("test.pdf", result.getNomeOriginal());
    verify(minioService, times(1)).uploadFile(any(), anyString());
    verify(arquivoRepository, times(1)).save(any(Arquivo.class));
  }
}
