package com.example.cityinfo.exception;

import com.example.cityinfo.config.ApiConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = Map.of(
                ApiConstants.FIELD_ERROR, ApiConstants.ERROR_INTERNAL_SERVER,
                ApiConstants.FIELD_MESSAGE, ex.getMessage(),
                ApiConstants.FIELD_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ApiConstants.FIELD_URL, ex.toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientResponseException(WebClientResponseException ex) {
        Map<String, Object> error = Map.of(
                ApiConstants.FIELD_ERROR, ApiConstants.ERROR_EXTERNAL_API,
                ApiConstants.FIELD_MESSAGE, ex.getResponseBodyAsString(),
                ApiConstants.FIELD_STATUS, ex.getStatusCode().value(),
                ApiConstants.FIELD_URL, ex.getRequest().getURI().toString()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> error = Map.of(
                ApiConstants.FIELD_ERROR, ApiConstants.ERROR_UNHANDLED,
                ApiConstants.FIELD_MESSAGE, ex.getMessage(),
                ApiConstants.FIELD_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ApiConstants.FIELD_DETAILS, ex.toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
