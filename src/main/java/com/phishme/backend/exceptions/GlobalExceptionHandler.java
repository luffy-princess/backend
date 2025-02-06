package com.phishme.backend.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.phishme.backend.dto.ErrorResponse;
import com.phishme.backend.enums.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, Exception ex,
            HttpServletRequest request) {
        String requestPath = request.getServletPath();
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());

        logErrorDetails(requestPath, errorCode, ex);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    private void logErrorDetails(String requestPath, ErrorCode errorCode, Exception ex) {
        log.warn(
                "Global Exception Handler\n\t[Request Path]: {}\n\t[Error Code]: {}\n\t[Error Message]: {}\n\t[Exception Message]: {}",
                requestPath, errorCode.getCode(), errorCode.getMessage(), ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getErrorCode(), ex, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.SERVICE_UNKOWN_ERROR_OCCURED, ex, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_SERVICE_REQUEST, ex, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_SERVICE_REQUEST, ex, request);
    }
}
