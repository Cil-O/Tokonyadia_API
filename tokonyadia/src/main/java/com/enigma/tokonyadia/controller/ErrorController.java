package com.enigma.tokonyadia.controller;

import com.enigma.tokonyadia.model.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<?>> apiException(ResponseStatusException exception) {
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .statusCode(exception.getStatus().value())
                .message(exception.getReason())
                .build();
        return ResponseEntity
                .status(exception.getStatus())
                .body(commonResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> apiException(ConstraintViolationException exception) {
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(commonResponse);
    }

}
