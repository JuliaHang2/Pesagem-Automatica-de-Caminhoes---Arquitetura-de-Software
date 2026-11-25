package br.edu.utfpr.pesagem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class TratadorErros {
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regra(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("erro", e.getMessage()));
    }
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(NaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
    }
}
