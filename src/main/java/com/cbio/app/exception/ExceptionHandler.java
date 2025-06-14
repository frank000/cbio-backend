package com.cbio.app.exception;


import jakarta.ws.rs.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CbioException> llegalExceptionHandler(IllegalArgumentException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new CbioException(e.getMessage(), HttpStatus.BAD_REQUEST.value())
                );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CbioException> forbiddenExceptionHandler(ForbiddenException e){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new CbioException(e.getMessage(), HttpStatus.FORBIDDEN.value())
                );
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(CbioException.class)
    public ResponseEntity<Map<String, String>> cbioExceptionHandler(CbioException e){

        return ResponseEntity
                .status(e.getStatus())
                .body(
                        Map.of("message", e.getMessage())
                );
    }
}
