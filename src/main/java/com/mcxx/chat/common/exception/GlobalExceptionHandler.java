package com.mcxx.chat.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import com.mcxx.chat.common.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentMismatchException(
      MethodArgumentTypeMismatchException ex) {
    String field = ex.getName();
    String value = ex.getValue() != null ? ex.getValue().toString() : "null";

    String message = String.format("Invalid value '%s' for field '%s'", value, field);

    return ResponseEntity.badRequest().body(ApiResponse.error(400, "INVALID_PARAMETER", message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {

    String message = "";
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      message = error.getDefaultMessage();
      if ("typeMismatch".equals(error.getCode())) {
        message = "Invalid value for field " + error.getField();
      }
      break;
    }

    return ResponseEntity.badRequest().body(ApiResponse.error(400, "VALIDATION_FAILED", message));
  }



  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(
      ResponseStatusException ex) {
    String errorCode = HttpStatus.valueOf(ex.getStatusCode().value()).name();
    return ResponseEntity.status(ex.getStatusCode())
        .body(ApiResponse.error(ex.getStatusCode().value(), errorCode, ex.getReason()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ApiResponse<Object>> handleMissingRequestHeaderException(
      MissingRequestHeaderException ex) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.error(400, "MISSING_HEADER", "Missing header: " + ex.getHeaderName()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest().body(ApiResponse.error(400, "INVALID_REQUEST_FORMAT",
        "Invalid request format: " + ex.getMostSpecificCause().getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(500, "INTERNAL_SERVER_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "Unknown error occurred"));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "NOT_FOUND",
        ex.getMessage() != null ? ex.getMessage() : "Not found"));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiResponse<Object>> handleConflictException(ConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(409, "CONFLICT", ex.getMessage()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(400, "BAD_REQUEST", ex.getMessage()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiResponse<Object>> handleForbiddenException(ForbiddenException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(403, "FORBIDDEN", ex.getMessage()));
  }
}
