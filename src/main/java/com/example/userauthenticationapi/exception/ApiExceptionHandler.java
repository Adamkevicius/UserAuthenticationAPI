package com.example.userauthenticationapi.exception;

import com.example.userauthenticationapi.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    private ResponseEntity<ApiErrorResponse> buildResponse(ApiErrorResponse apiErrorResponse, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(apiErrorResponse);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<ApiErrorResponse> handleForbidden(ForbiddenException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException e) {
        return buildResponse(
                new ApiErrorResponse(
                        false,
                        e.getMessage(),
                        LocalDateTime.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
