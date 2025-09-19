package com.priya.userservice.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.priya.userservice.dto.ErrorResponseDTO;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@code GlobalExceptionHandler} centralizes exception handling across
 * all REST controllers in the Auth Service.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Converts exceptions into standardized {@link ErrorResponseDTO} objects</li>
 *   <li>Ensures consistent HTTP status codes and error structure for clients</li>
 *   <li>Prevents leaking sensitive details such as stack traces</li>
 *   <li>Provides structured logging for observability (WARN vs ERROR severity)</li>
 * </ul>
 *
 * <p>This class ensures that all exceptions are captured and logged
 * with context (request path, message), making debugging and monitoring easier.</p>
 */

@Slf4j
@RestControllerAdvice
@Tag(name = "Global Exception Handling", description = "Handles all application-wide exceptions for Auth Service APIs")
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles cases when a requested resource is not found.
     *
     * @param ex      the thrown {@link ResourceNotFoundException}
     * @param request HttpServletRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 404 status
     */
    @Operation(summary = "Handle Resource Not Found",
            description = "Triggered when a requested resource does not exist.")
    @ApiResponse(responseCode = "404", description = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex,
                                                                   HttpServletRequest request) {
        log.warn("ResourceNotFoundException at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles cases when attempting to create a resource that already exists.
     *
     * @param ex      the thrown {@link ResourceAlreadyExistsException}
     * @param request HttpServletRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 409 status
     */
    @Operation(summary = "Handle Duplicate Resource",
            description = "Triggered when attempting to create a resource that already exists.")
    @ApiResponse(responseCode = "409", description = "Conflict - resource already exists")
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResource(ResourceAlreadyExistsException ex,
                                                                    HttpServletRequest request) {
        log.warn("ResourceAlreadyExistsException at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles validation errors thrown by Spring's validation annotations
     * (e.g., {@code @NotBlank}, {@code @Email}, etc.).
     *
     * @param ex      the thrown {@link MethodArgumentNotValidException}
     * @param request HttpServletRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 400 status
     */
    @Operation(summary = "Handle Validation Errors",
            description = "Triggered when request body fails bean validation.")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        String errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error at [{}]: {}", request.getRequestURI(), errorMessages);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessages, request.getRequestURI());
    }

    /**
     * Handles unauthorized access attempts when a user is authenticated
     * but lacks the required permission.
     *
     * @param ex      the thrown {@link AccessDeniedException}
     * @param request WebRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 403 status
     */
    @Operation(summary = "Handle Access Denied",
            description = "Triggered when a user tries to access a resource without sufficient permissions.")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {

        String path = Optional.ofNullable(((ServletWebRequest) request).getRequest().getRequestURI())
                .orElse("N/A");
        log.warn("AccessDeniedException at [{}]: {}", path, ex.getMessage());

        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), path);
    }

    /**
     * Handles authentication failures (invalid credentials, expired tokens).
     */
    @Operation(summary = "Handle Authentication Exception",
            description = "Triggered when authentication fails (e.g., bad credentials, expired token).")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex,
            HttpServletRequest request) {
        log.warn("AuthenticationException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
    }


    /**
     * Handles invalid state transitions or operations that violate business logic.
     *
     * @param ex      the thrown {@link IllegalStateException}
     * @param request HttpServletRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 400 status
     */
    @Operation(summary = "Handle Illegal State",
            description = "Triggered when a business logic constraint is violated.")
    @ApiResponse(responseCode = "400", description = "Bad Request due to invalid state")
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalState(IllegalStateException ex,
                                                               HttpServletRequest request) {
        log.error("IllegalStateException at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles downstream service failures (via Feign clients).
     */
    @Operation(summary = "Handle Feign Exception",
            description = "Triggered when downstream microservice communication fails.")
    @ApiResponse(responseCode = "502", description = "Bad Gateway - downstream service error")
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDTO> handleFeignException(FeignException ex, HttpServletRequest request) {
        String responseBody = ex.contentUTF8();
        ErrorResponseDTO errorResponse;

        try {
            JsonNode node = objectMapper.readTree(responseBody);
            errorResponse = ErrorResponseDTO.builder()
                    .timeStamp(LocalDateTime.now())
                    .status(ex.status())
                    .error(node.path("error").asText(HttpStatus.valueOf(ex.status()).getReasonPhrase()))
                    .message(node.path("message").asText("Downstream service error"))
                    .path(request.getRequestURI())
                    .build();
        } catch (Exception parseEx) {
            log.error("Failed to parse FeignException body: {}", responseBody, parseEx);
            errorResponse = ErrorResponseDTO.builder()
                    .timeStamp(LocalDateTime.now())
                    .status(ex.status())
                    .error(HttpStatus.valueOf(ex.status()).getReasonPhrase())
                    .message("Downstream service error")
                    .path(request.getRequestURI())
                    .build();
        }
        log.error("FeignException [{}] at [{}]: {}", ex.status(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(ex.status()).body(errorResponse);
    }

    @Operation(summary = "Handle Invalid Credentials",
            description = "Triggered when login credentials are invalid.")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {

        log.error("InvalidCredentialsException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
    }

    @Operation(summary = "Handle Invalid User State",
            description = "Triggered when a user account is in an invalid state (e.g., suspended, deactivated).")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ExceptionHandler(InvalidUserStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUserState(
            InvalidUserStateException ex, HttpServletRequest request) {

        log.error("InvalidUserStateException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles all uncaught exceptions. Prevents stack traces or sensitive
     * details from being exposed to clients.
     *
     * @param ex      the thrown {@link Exception}
     * @param request HttpServletRequest for extracting the request URI
     * @return {@link ErrorResponseDTO} with HTTP 500 status
     */
    @Operation(summary = "Handle Generic Exception",
            description = "Fallback for all uncaught exceptions. Returns HTTP 500.")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex,
                                                                   HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred", request.getRequestURI());
    }

    /**
     * Utility method to build a consistent {@link ErrorResponseDTO}.
     *
     * @param status  the HTTP status to return
     * @param error   short error type description
     * @param message detailed error message
     * @param path    API endpoint path
     * @return ResponseEntity with standardized error payload
     */
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status,
                                                                String error,
                                                                String message,
                                                                String path) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .timeStamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(response);
    }

}
