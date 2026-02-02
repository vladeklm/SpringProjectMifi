package com.example.hotelservice.exception;

import com.example.hotelservice.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), java.time.LocalDateTime.now(), getTraceId(request));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), ex.getMessage(), java.time.LocalDateTime.now(), getTraceId(request));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409 для конфликтов бронирования
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneral(Exception ex, WebRequest request) {
        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", java.time.LocalDateTime.now(), getTraceId(request));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getTraceId(WebRequest request) {
        // Spring Sleuth/Micrometer устанавливает traceId в заголовки, если включен
        return request.getHeader("X-B3-TraceId");
    }
}