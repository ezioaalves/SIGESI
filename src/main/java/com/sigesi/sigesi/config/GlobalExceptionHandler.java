package com.sigesi.sigesi.config;

import com.sigesi.sigesi.arquivos.validation.InvalidFileException;
import com.sigesi.sigesi.storage.StorageException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of(
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Não encontrado",
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
            "error", "Dados inválidos",
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

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<Map<String, Object>> handleStorage(StorageException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Erro de armazenamento",
            "message", "Não foi possível processar o arquivo no armazenamento"));
  }

  @ExceptionHandler(InvalidFileException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidFile(InvalidFileException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Arquivo inválido",
            "message", ex.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, Object>> handleMaxUploadSize(
      MaxUploadSizeExceededException ex) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(Map.of(
            "status", HttpStatus.PAYLOAD_TOO_LARGE.value(),
            "error", "Arquivo muito grande",
            "message", "O arquivo excede o limite máximo de 5 MB"));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadableBody(
      HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Dados inválidos",
            "message", "Não foi possível interpretar os dados enviados"));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
    String message = ex.getReason() != null ? ex.getReason() : "Não foi possível concluir a operação";
    return ResponseEntity.status(ex.getStatusCode())
        .body(Map.of(
            "status", ex.getStatusCode().value(),
            "error", errorLabel(ex.getStatusCode().value()),
            "message", message));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Erro interno",
            "message", "Não foi possível concluir a operação"));
  }

  private String errorLabel(int status) {
    return switch (status) {
      case 400 -> "Dados inválidos";
      case 401 -> "Não autenticado";
      case 403 -> "Acesso negado";
      case 404 -> "Não encontrado";
      case 409 -> "Conflito";
      case 413 -> "Arquivo muito grande";
      default -> "Erro na operação";
    };
  }
}
