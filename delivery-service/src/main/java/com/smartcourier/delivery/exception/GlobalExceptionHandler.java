package com.smartcourier.delivery.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    DeliveryNotFoundException
    @ExceptionHandler(DeliveryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            DeliveryNotFoundException ex, HttpServletRequest req) {

        return new ResponseEntity<>(
                new ErrorResponse(LocalDateTime.now(), 404, "NOT_FOUND",
                        ex.getMessage(), req.getRequestURI()),
                HttpStatus.NOT_FOUND
        );
    }

//    InvalidStatusException
    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            InvalidStatusException ex, HttpServletRequest req) {

        return new ResponseEntity<>(
                new ErrorResponse(LocalDateTime.now(), 400, "BAD_REQUEST",
                        ex.getMessage(), req.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        return new ResponseEntity<>(
                new ErrorResponse(LocalDateTime.now(), 400, "BAD_REQUEST",
                        message, req.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest req) {

        return new ResponseEntity<>(
                new ErrorResponse(LocalDateTime.now(), 500, "ERROR",
                        ex.getMessage(), req.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
