package com.sigesi.sigesi.arquivos.validation;

/**
 * Exception for file validation errors.
 */
public class InvalidFileException extends RuntimeException {
  public InvalidFileException(String message) {
    super(message);
  }
}
