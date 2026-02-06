package com.sigesi.sigesi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;

/**
 * Testes unitarios para MinioService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MinioService Tests")
class MinioServiceTest {

  @Mock
  private MinioClient minioClient;

  @Mock
  private GetObjectResponse getObjectResponse;

  private MinioService minioService;

  @BeforeEach
  void setUp() {
    minioService = new MinioService(minioClient);
    ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
  }

  @Test
  @DisplayName("Deve criar bucket quando nao existe")
  void testInitBucketCreatesWhenNotExists() throws Exception {
    when(minioClient.bucketExists(any())).thenReturn(false);

    minioService.initBucket();

    verify(minioClient, times(1)).makeBucket(any());
  }

  @Test
  @DisplayName("Deve pular criacao quando bucket ja existe")
  void testInitBucketSkipsWhenExists() throws Exception {
    when(minioClient.bucketExists(any())).thenReturn(true);

    minioService.initBucket();

    verify(minioClient, times(0)).makeBucket(any());
  }

  @Test
  @DisplayName("Deve lancar StorageException quando initBucket falha")
  void testInitBucketThrowsStorageException() throws Exception {
    when(minioClient.bucketExists(any())).thenThrow(new RuntimeException("Connection failed"));

    assertThrows(StorageException.class, () -> minioService.initBucket());
  }

  @Test
  @DisplayName("Deve fazer upload de arquivo com sucesso")
  void testUploadFileComSucesso() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf", "data".getBytes());

    String result = minioService.uploadFile(file, "test-key");

    assertEquals("test-key", result);
    verify(minioClient, times(1)).putObject(any());
  }

  @Test
  @DisplayName("Deve lancar StorageException quando upload falha")
  void testUploadFileThrowsStorageException() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.pdf", "application/pdf", "data".getBytes());

    when(minioClient.putObject(any())).thenThrow(new RuntimeException("Upload failed"));

    assertThrows(StorageException.class,
        () -> minioService.uploadFile(file, "test-key"));
  }

  @Test
  @DisplayName("Deve fazer download de arquivo com sucesso")
  void testDownloadFileComSucesso() throws Exception {
    when(minioClient.getObject(any())).thenReturn(getObjectResponse);

    InputStream result = minioService.downloadFile("test-key");

    assertNotNull(result);
  }

  @Test
  @DisplayName("Deve lancar StorageException quando download falha")
  void testDownloadFileThrowsStorageException() throws Exception {
    when(minioClient.getObject(any())).thenThrow(new RuntimeException("Download failed"));

    assertThrows(StorageException.class,
        () -> minioService.downloadFile("test-key"));
  }

  @Test
  @DisplayName("Deve gerar presigned URL com sucesso")
  void testGetPresignedUrlComSucesso() throws Exception {
    when(minioClient.getPresignedObjectUrl(any())).thenReturn("https://minio/url");

    String result = minioService.getPresignedUrl("test-key", 60);

    assertEquals("https://minio/url", result);
  }

  @Test
  @DisplayName("Deve lancar StorageException quando geracao de URL falha")
  void testGetPresignedUrlThrowsStorageException() throws Exception {
    when(minioClient.getPresignedObjectUrl(any())).thenThrow(new RuntimeException("URL failed"));

    assertThrows(StorageException.class,
        () -> minioService.getPresignedUrl("test-key", 60));
  }

  @Test
  @DisplayName("Deve deletar arquivo com sucesso")
  void testDeleteFileComSucesso() throws Exception {
    minioService.deleteFile("test-key");

    verify(minioClient, times(1)).removeObject(any());
  }

  @Test
  @DisplayName("Deve lancar StorageException quando delecao falha")
  void testDeleteFileThrowsStorageException() throws Exception {
    doThrow(new RuntimeException("Delete failed")).when(minioClient).removeObject(any());

    assertThrows(StorageException.class,
        () -> minioService.deleteFile("test-key"));
  }

  @Test
  @DisplayName("Deve retornar true quando arquivo existe")
  void testFileExistsReturnsTrue() throws Exception {
    when(minioClient.statObject(any())).thenReturn(null);

    boolean result = minioService.fileExists("test-key");

    assertTrue(result);
  }

  @Test
  @DisplayName("Deve retornar false quando arquivo nao existe")
  void testFileExistsReturnsFalse() throws Exception {
    when(minioClient.statObject(any())).thenThrow(new RuntimeException("Not found"));

    boolean result = minioService.fileExists("test-key");

    assertFalse(result);
  }
}
