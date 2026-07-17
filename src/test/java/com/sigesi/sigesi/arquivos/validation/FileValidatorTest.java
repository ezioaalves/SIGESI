package com.sigesi.sigesi.arquivos.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Testes unitarios para FileValidator.
 */
@DisplayName("FileValidator Tests")
class FileValidatorTest {

  @Test
  @DisplayName("Deve validar JPEG valido com sucesso")
  void testValidateFileValidJpeg() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "photo.jpg", "image/jpeg", "image data".getBytes());

    assertDoesNotThrow(() -> FileValidator.validateFile(file));
  }

  @Test
  @DisplayName("Deve validar PDF valido com sucesso")
  void testValidateFileValidPdf() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "document.pdf", "application/pdf", "pdf data".getBytes());

    assertDoesNotThrow(() -> FileValidator.validateFile(file));
  }

  @Test
  @DisplayName("Deve lancar excecao para arquivo vazio")
  void testValidateFileSizeEmptyFile() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "empty.jpg", "image/jpeg", new byte[0]);

    InvalidFileException ex = assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFileSize(file));
    assertTrue(ex.getMessage().contains("vazio"));
  }

  @Test
  @DisplayName("Deve lancar excecao para arquivo maior que 5 MB")
  void testValidateFileSizeOversizedFile() {
    byte[] largeContent = new byte[(5 * 1024 * 1024) + 1];
    MockMultipartFile file = new MockMultipartFile(
        "file", "large.jpg", "image/jpeg", largeContent);

    InvalidFileException ex = assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFileSize(file));
    assertTrue(ex.getMessage().contains("5 MB"));
  }

  @Test
  @DisplayName("Deve aceitar arquivo com exatamente 5 MB")
  void testValidateFileSizeAtLimit() {
    byte[] content = new byte[5 * 1024 * 1024];
    MockMultipartFile file = new MockMultipartFile(
        "file", "imagem grande.png", "image/png", content);

    assertDoesNotThrow(() -> FileValidator.validateFile(file));
  }

  @Test
  @DisplayName("Deve aceitar PNG, WEBP, DOC e DOCX")
  void testValidateAdditionalAllowedFormats() {
    assertDoesNotThrow(() -> FileValidator.validateFile(new MockMultipartFile(
        "file", "imagem com espaços.png", "image/png", "png".getBytes())));
    assertDoesNotThrow(() -> FileValidator.validateFile(new MockMultipartFile(
        "file", "imagem.webp", "image/webp", "webp".getBytes())));
    assertDoesNotThrow(() -> FileValidator.validateFile(new MockMultipartFile(
        "file", "documento.doc", "application/msword", "doc".getBytes())));
    assertDoesNotThrow(() -> FileValidator.validateFile(new MockMultipartFile(
        "file", "documento.docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "docx".getBytes())));
  }

  @Test
  @DisplayName("Deve rejeitar GIF e TIFF")
  void testRejectForbiddenImageFormats() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFile(new MockMultipartFile(
            "file", "animacao.gif", "image/gif", "gif".getBytes())));
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFile(new MockMultipartFile(
            "file", "imagem.tiff", "image/tiff", "tiff".getBytes())));
  }

  @Test
  @DisplayName("Deve lancar excecao para content type invalido")
  void testValidateContentTypeInvalidType() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "script.exe", "application/x-executable", "data".getBytes());

    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateContentType(file));
  }

  @Test
  @DisplayName("Deve lancar excecao para content type nulo")
  void testValidateContentTypeNullType() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", null, "data".getBytes());

    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateContentType(file));
  }

  @Test
  @DisplayName("Deve lancar excecao para extensao invalida")
  void testValidateFileExtensionInvalidExtension() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFileExtension("malware.exe"));
  }

  @Test
  @DisplayName("Deve lancar excecao para arquivo sem extensao")
  void testValidateFileExtensionNoExtension() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFileExtension("noextension"));
  }

  @Test
  @DisplayName("Deve lancar excecao para path traversal com ../")
  void testValidateFilenamePathTraversal() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFilename("../file.jpg"));
  }

  @Test
  @DisplayName("Deve lancar excecao para path traversal com barra invertida")
  void testValidateFilenameBackslashTraversal() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFilename("..\\file.jpg"));
  }

  @Test
  @DisplayName("Deve lancar excecao para filename com null byte")
  void testValidateFilenameNullBytes() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFilename("file\0.jpg"));
  }

  @Test
  @DisplayName("Deve lancar excecao para filename vazio")
  void testValidateFilenameEmptyName() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFilename(""));
  }

  @Test
  @DisplayName("Deve lancar excecao para filename nulo")
  void testValidateFilenameNullName() {
    assertThrows(InvalidFileException.class,
        () -> FileValidator.validateFilename(null));
  }

  @Test
  @DisplayName("Deve passar todas as validacoes para arquivo valido")
  void testValidateFileAllValidationsPass() {
    MockMultipartFile file = new MockMultipartFile(
        "file", "report.pdf", "application/pdf", "pdf content".getBytes());

    assertDoesNotThrow(() -> FileValidator.validateFile(file));
  }
}
