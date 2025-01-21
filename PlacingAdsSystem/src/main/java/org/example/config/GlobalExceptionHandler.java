package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.dto.ErrorResponse;
import org.example.exceptions.AdsNotFoundException;
import org.example.exceptions.CommentNotFoundException;
import org.example.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AdsNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAdsNotFoundException(AdsNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

}
