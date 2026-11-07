package com.immersion.hub.exception;

import com.immersion.hub.dto.ErrorDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDto> notFound(NotFoundException e) {
    return build(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorDto> unauthorized(UnauthorizedException e) {
    return build(HttpStatus.UNAUTHORIZED, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return build(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorDto> badRequest(BadRequestException e) {
    return build(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleGenericException(Exception e) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }

  private ResponseEntity<ErrorDto> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorDto(status.value(), message, Instant.now()));
  }
}
