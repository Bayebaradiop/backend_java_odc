package com.medibook.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Réponse API standardisée pour toutes les endpoints
 * 
 * @param <T> Type de données retourné
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiStandardResponse<T> {

    /**
     * Indique si la requête a réussi
     */
    private boolean success;

    /**
     * Message descriptif de l'opération
     */
    private String message;

    /**
     * Données retournées par l'API (peut être null en cas d'erreur)
     */
    private T data;

    /**
     * Détails de l'erreur ( uniquement en cas d'erreur)
     */
    private ErrorDetail error;

    /**
     * Timestamp de la réponse
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Métadonnées supplémentaires (pagination, etc.)
     */
    private Metadata metadata;

    /**
     * Détails d'erreur structurée
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private String code;
        private String description;
        private Object details;
    }

    /**
     * Métadonnées pour la pagination et autres infos
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metadata {
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
    }

    // ==================== Méthodes utilitaires de succès ====================

    /**
     * Crée une réponse de succès avec données
     */
    public static <T> ApiStandardResponse<T> success(T data) {
        return ApiStandardResponse.<T>builder()
                .success(true)
                .message("Opération réussie")
                .data(data)
                .build();
    }

    /**
     * Crée une réponse de succès avec message personnalisé
     */
    public static <T> ApiStandardResponse<T> success(T data, String message) {
        return ApiStandardResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crée une réponse de succès sans données (pour suppression)
     */
    public static <T> ApiStandardResponse<T> success(String message) {
        return ApiStandardResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    // ==================== Méthodes utilitaires d'erreur ====================

    /**
     * Crée une réponse d'erreur
     */
    public static <T> ApiStandardResponse<T> error(String message) {
        return ApiStandardResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetail.builder()
                        .description(message)
                        .build())
                .build();
    }

    /**
     * Crée une réponse d'erreur avec code
     */
    public static <T> ApiStandardResponse<T> error(String message, String code) {
        return ApiStandardResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetail.builder()
                        .code(code)
                        .description(message)
                        .build())
                .build();
    }

    /**
     * Crée une réponse d'erreur avec code et détails
     */
    public static <T> ApiStandardResponse<T> error(String message, String code, Object details) {
        return ApiStandardResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetail.builder()
                        .code(code)
                        .description(message)
                        .details(details)
                        .build())
                .build();
    }
}
