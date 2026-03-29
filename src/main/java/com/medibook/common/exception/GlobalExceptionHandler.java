package com.medibook.common.exception;

import com.medibook.common.dto.ApiStandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERR_NOT_FOUND = "ERR_NOT_FOUND";
    private static final String ERR_FORBIDDEN = "ERR_FORBIDDEN";
    private static final String ERR_VALIDATION = "ERR_VALIDATION";
    private static final String ERR_BUSINESS = "ERR_BUSINESS";
    private static final String ERR_FILE_TOO_LARGE = "ERR_FILE_TOO_LARGE";
    private static final String ERR_INTERNAL = "ERR_INTERNAL";
    private static final String ERR_MISSING_PARAM = "ERR_MISSING_PARAM";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_NOT_FOUND));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error("Endpoint non trouvé", ERR_NOT_FOUND));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_NOT_FOUND));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_FORBIDDEN));
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleFieldValidationException(FieldValidationException ex) {
        log.warn("Field validation error: {}", ex.getFieldErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_VALIDATION, ex.getFieldErrors()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleBusinessException(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_BUSINESS));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("Missing parameter: {}", ex.getMessage());
        String fieldName = ex.getParameterName();
        String message = "Le paramètre '" + fieldName + "' est obligatoire";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(message, ERR_MISSING_PARAM, Map.of("parameter", fieldName)));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error("Erreur de validation", ERR_VALIDATION, errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("File too large: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiStandardResponse.error("Le fichier est trop volumineux (max: 10MB)", ERR_FILE_TOO_LARGE));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        
        String message = ex.getMessage();
        String userFriendlyMessage = "Une erreur de données est survenue";
        
        if (message != null) {
            if (message.contains("utilisateurs_telephone_key") || message.contains("telephone")) {
                userFriendlyMessage = "Ce numéro de téléphone est déjà utilisé par un autre utilisateur";
            } else if (message.contains("utilisateurs_email_key") || message.contains("email")) {
                userFriendlyMessage = "Cette adresse email est déjà utilisée par un autre utilisateur";
            } else if (message.contains("duplicate key")) {
                Pattern pattern = Pattern.compile("Key \\((.*?)\\)=");
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    String field = matcher.group(1);
                    userFriendlyMessage = "La valeur '" + field + "' existe déjà";
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(userFriendlyMessage, ERR_BUSINESS));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiStandardResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiStandardResponse.error(ex.getMessage(), ERR_BUSINESS));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiStandardResponse<?>> handleException(Exception ex) {
        log.error("Internal error: {}", ex.getMessage(), ex);
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Une erreur interne est survenue";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiStandardResponse.error(errorMessage, ERR_INTERNAL));
    }
}
