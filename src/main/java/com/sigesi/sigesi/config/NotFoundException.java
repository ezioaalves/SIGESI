package com.sigesi.sigesi.config;

/**
 * Exceção para recursos não encontrados.
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
