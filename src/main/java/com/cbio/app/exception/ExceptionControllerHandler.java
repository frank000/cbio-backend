package com.cbio.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionControllerHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
    public  ResponseEntity<Map<String, String>> throwable(Throwable e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> responseStatusException(ResponseStatusException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CbioException.class)
    public ResponseEntity<Map<String, String>> cbioExceptionHandler(CbioException e) {
        log.error(e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage()); // Adiciona a mensagem de erro no formato JSON

        return ResponseEntity.status(e.getStatus()).body(response);
    }


}
