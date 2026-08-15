package com.medibook.ExceptionsPlanning.controller;

import com.medibook.ExceptionsPlanning.dto.ExceptionRequest;
import com.medibook.ExceptionsPlanning.dto.ExceptionResponse;
import com.medibook.ExceptionsPlanning.message.MessageErreur;
import com.medibook.ExceptionsPlanning.message.MessageSucces;
import com.medibook.ExceptionsPlanning.service.ExceptionService;
import com.medibook.common.dto.ApiStandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des exceptions de planning par le médecin connecté
 */
@RestController
@RequestMapping("/api/medecin/exceptions")
@RequiredArgsConstructor
@Tag(name = "Exceptions Planning - Médecin", description = "API de gestion des exceptions de planning pour les médecins")
@Slf4j
public class MedecinExceptionController {

    private final ExceptionService exceptionService;

    @Operation(summary = "Mes exceptions de planning", description = "Retourne toutes les exceptions de planning du médecin connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.EXCEPTIONS_RECUPEREES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<List<ExceptionResponse>>> getMesExceptions() {
        List<ExceptionResponse> exceptions = exceptionService.getMesExceptions();
        return ResponseEntity.ok(ApiStandardResponse.success(exceptions, MessageSucces.EXCEPTIONS_RECUPEREES));
    }

    @Operation(summary = "Créer une exception de planning", description = "Crée une exception de planning pour le médecin connecté (indisponibilité, congé, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = MessageSucces.EXCEPTION_CREEE),
            @ApiResponse(responseCode = "400", description = MessageErreur.HEURE_INVALIDE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @PostMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<ExceptionResponse>> creerException(
            @Parameter(description = "Données de l'exception")
            @Valid @RequestBody ExceptionRequest request) {
        ExceptionResponse response = exceptionService.creerExceptionMedecin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.EXCEPTION_CREEE));
    }

    @Operation(summary = "Modifier une exception de planning", description = "Modifie une exception de planning appartenant au médecin connecté")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<ExceptionResponse>> modifierException(
            @Parameter(description = "ID de l'exception") @PathVariable Long id,
            @Valid @RequestBody ExceptionRequest request) {
        ExceptionResponse response = exceptionService.modifierExceptionMedecin(id, request);
        return ResponseEntity.ok(ApiStandardResponse.success(response, "Exception de planning mise à jour avec succès"));
    }

    @Operation(summary = "Supprimer une exception de planning", description = "Supprime une exception de planning appartenant au médecin connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.EXCEPTION_SUPPRIMEE),
            @ApiResponse(responseCode = "404", description = MessageErreur.EXCEPTION_NON_TROUVEE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_REFUSE)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<Void>> supprimerException(
            @Parameter(description = "ID de l'exception") @PathVariable Long id) {
        exceptionService.supprimerException(id);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.EXCEPTION_SUPPRIMEE));
    }
}
