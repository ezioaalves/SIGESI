package com.sigesi.sigesi.arquivos.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility for generating unique storage keys.
 */
public final class StorageKeyGenerator {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy/MM/dd");

  private StorageKeyGenerator() {
    // Utility class
  }

  /**
   * Generate date-based key with UUID.
   * Example: "2024/12/29/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
   */
  public static String generateKey(String originalFilename) {
    String datePath = LocalDate.now().format(DATE_FORMATTER);
    String extension = getFileExtension(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return String.format("%s/%s%s", datePath, uuid,
        extension.isEmpty() ? "" : "." + extension);
  }

  /**
   * Generate category-based key.
   * Example: "solicitacoes/2024/12/29/uuid.pdf"
   */
  public static String generateCategoryKey(String categoria, String originalFilename) {
    String datePath = LocalDate.now().format(DATE_FORMATTER);
    String extension = getFileExtension(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return String.format("%s/%s/%s%s", categoria.toLowerCase(), datePath, uuid,
        extension.isEmpty() ? "" : "." + extension);
  }

  private static String getFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
      return "";
    }
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1).toLowerCase();
  }
}
