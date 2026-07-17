package com.sigesi.sigesi.arquivos.validation;

import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 * File validation utility.
 */
public final class FileValidator {

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
      "image/jpeg",
      "image/png",
      "image/webp",
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
      "jpg", "jpeg", "png", "webp", "pdf", "doc", "docx"
  );

  private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5 MiB
  private static final int MAX_FILENAME_LENGTH = 255;

  private FileValidator() {
    // Utility class
  }

  /**
   * Validate file size.
   */
  public static void validateFileSize(MultipartFile file) {
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new InvalidFileException("O arquivo excede o limite máximo de 5 MB");
    }
    if (file.isEmpty()) {
      throw new InvalidFileException("O arquivo está vazio");
    }
  }

  /**
   * Validate content type.
   */
  public static void validateContentType(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new InvalidFileException("Tipo de arquivo não permitido: " + contentType);
    }
  }

  /**
   * Validate file extension.
   */
  public static void validateFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new InvalidFileException("O nome do arquivo está vazio");
    }

    String extension = getFileExtension(filename).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new InvalidFileException("Extensão de arquivo não permitida: " + extension);
    }
  }

  /**
   * Validate filename for security.
   */
  public static void validateFilename(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new InvalidFileException("O nome do arquivo está vazio");
    }

    if (filename.length() > MAX_FILENAME_LENGTH) {
      throw new InvalidFileException("O nome do arquivo deve ter no máximo 255 caracteres");
    }

    // Check for path traversal
    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
      throw new InvalidFileException("O nome do arquivo contém caracteres não permitidos");
    }

    // Check for null bytes
    if (filename.contains("\0")) {
      throw new InvalidFileException("O nome do arquivo contém um caractere nulo");
    }
  }

  /**
   * Complete file validation.
   */
  public static void validateFile(MultipartFile file) {
    validateFilename(file.getOriginalFilename());
    validateFileSize(file);
    validateContentType(file);
    validateFileExtension(file.getOriginalFilename());
  }

  private static String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1);
  }
}
