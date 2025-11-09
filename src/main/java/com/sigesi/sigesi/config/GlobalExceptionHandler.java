package com.sigesi.sigesi.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of(
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Not Found",
            "message", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getAllErrors().stream()
        .findFirst()
        .map(objectError -> objectError.getDefaultMessage())
        .orElse("Dados inválidos");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", message));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of(
            "status", HttpStatus.CONFLICT.value(),
            "error", "Conflito",
            "message", ex.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Internal Server Error",
            "message", ex.getMessage()));
  }
}
