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
      "image/gif",
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
      "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"
  );

  private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB

  private FileValidator() {
    // Utility class
  }

  /**
   * Validate file size.
   */
  public static void validateFileSize(MultipartFile file) {
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new InvalidFileException("File size exceeds 10MB limit");
    }
    if (file.isEmpty()) {
      throw new InvalidFileException("File is empty");
    }
  }

  /**
   * Validate content type.
   */
  public static void validateContentType(MultipartFile file) {
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new InvalidFileException("Invalid file type: " + contentType);
    }
  }

  /**
   * Validate file extension.
   */
  public static void validateFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new InvalidFileException("Filename is empty");
    }

    String extension = getFileExtension(filename).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new InvalidFileException("Invalid file extension: " + extension);
    }
  }

  /**
   * Validate filename for security.
   */
  public static void validateFilename(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new InvalidFileException("Filename is empty");
    }

    // Check for path traversal
    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
      throw new InvalidFileException("Invalid filename: contains illegal characters");
    }

    // Check for null bytes
    if (filename.contains("\0")) {
      throw new InvalidFileException("Invalid filename: contains null byte");
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
