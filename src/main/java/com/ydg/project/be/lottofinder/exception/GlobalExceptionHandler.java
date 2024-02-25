package com.ydg.project.be.lottofinder.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> invalidMemberExceptionHandler(WebExchangeBindException ex) {

        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        Map<String, String> errors = new HashMap<>();

        fieldErrorList.forEach(err -> {
            errors.put("field", err.getField());
            errors.put("errMsg", err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
