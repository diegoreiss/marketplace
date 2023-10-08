package com.springboot.marketplace.handler;

import com.springboot.marketplace.dto.response.StandardErrorResponseDTO;
import com.springboot.marketplace.exception.ConfirmationTokenException;
import com.springboot.marketplace.exception.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardErrorResponseDTO> invalidCredentials(AuthenticationException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        List.of(),
                        e.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<StandardErrorResponseDTO> emailAlreadyExists(EmailAlreadyExistsException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new StandardErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        List.of(),
                        e.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorResponseDTO> validationFailed(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : e.getFieldErrors()) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(new StandardErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.PRECONDITION_FAILED.value(),
                        errors,
                        "Requisição inválida",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(ConfirmationTokenException.class)
    public ResponseEntity<String> tokenConfirmedOrExpired(ConfirmationTokenException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token confirmed or expired");
    }
}
