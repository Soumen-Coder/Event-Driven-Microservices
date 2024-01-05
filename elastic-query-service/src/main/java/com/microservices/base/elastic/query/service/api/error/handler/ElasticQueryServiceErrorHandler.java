package com.microservices.base.elastic.query.service.api.error.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // enables global exception handling in spring boot, used with @ExceptionHandler
public class ElasticQueryServiceErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticQueryServiceErrorHandler.class);
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handle(AccessDeniedException e){
        LOG.error("Access Denied Exception!", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this resource!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException e){
        LOG.error("Illegal Argument Exception", e);
        return ResponseEntity.badRequest().body("Illegal argument exception!" +e.getMessage()); //related with client hence badRequest() is being used
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e){
        LOG.error("Service runtime exception!");
        return ResponseEntity.badRequest().body("Service runtime exception!" +e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e){
        LOG.error("Internal sever error!", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A server error occurred"); //related with server hence status() is being used
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e){
        LOG.error("Method argument validation exception!", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError)error).getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
