package com.sigesi.sigesi.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EnumExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> tratarErrosDeEnum(HttpMessageNotReadableException ex) {
    Map<String, String> resposta = new HashMap<>();

    Throwable causa = ex.getCause();
    if (causa instanceof InvalidFormatException invalidFormatException) {
      Class<?> tipoAlvo = invalidFormatException.getTargetType();
      if (tipoAlvo.isEnum()) {
        String valoresAceitos = String.join(", ",
            Arrays.stream(tipoAlvo.getEnumConstants())
                .map(Object::toString)
                .toArray(String[]::new));

        resposta.put("erro", "Valor inválido para o enum " + tipoAlvo.getSimpleName());
        resposta.put("mensagem", "Os valores aceitos são: [" + valoresAceitos + "]");
        return new ResponseEntity<>(resposta, HttpStatus.BAD_REQUEST);
      }
    }

    resposta.put("erro", "Requisição inválida");
    resposta.put("mensagem", ex.getMessage());
    return new ResponseEntity<>(resposta, HttpStatus.BAD_REQUEST);
  }
}
