package com.medibook.ExceptionsPlanning.controller;

import com.medibook.ExceptionsPlanning.dto.ExceptionRequest;
import com.medibook.ExceptionsPlanning.dto.ExceptionResponse;
import com.medibook.ExceptionsPlanning.message.MessageErreur;
import com.medibook.ExceptionsPlanning.message.MessageSucces;
import com.medibook.ExceptionsPlanning.service.ExceptionService;
import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
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
 * Contrôleur pour la gestion des exceptions de planning par la secrétaire
 */
@RestController
@RequestMapping("/api/secretaire/exceptions")
@RequiredArgsConstructor
@Tag(name = "Exceptions Planning - Secrétaire", description = "API de gestion des exceptions de planning pour les secrétaires")
@Slf4j
public class SecretaireExceptionController {

    private final ExceptionService exceptionService;
        private final JwtUserUtil jwtUserUtil;

    @Operation(summary = "Exceptions d'un médecin", description = "Liste toutes les exceptions de planning d'un médecin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.EXCEPTIONS_RECUPEREES),
            @ApiResponse(responseCode = "404", description = MessageErreur.MEDECIN_NON_TROUVE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SPECIALITE)
    })
    @GetMapping("/medecin/{medecinId}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<List<ExceptionResponse>>> getExceptionsMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long medecinId) {
                Long userId = jwtUserUtil.getCurrentUserId();
                List<ExceptionResponse> exceptions = exceptionService.getExceptionsByMedecin(medecinId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(exceptions, MessageSucces.EXCEPTIONS_RECUPEREES));
    }

    @Operation(summary = "Créer une exception pour un médecin", description = "Crée une exception de planning pour un médecin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = MessageSucces.EXCEPTION_CREEE),
            @ApiResponse(responseCode = "400", description = MessageErreur.HEURE_INVALIDE),
            @ApiResponse(responseCode = "404", description = MessageErreur.MEDECIN_NON_TROUVE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SPECIALITE)
    })
    @PostMapping("/medecin/{medecinId}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<ExceptionResponse>> creerExceptionPourMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long medecinId,
            @Parameter(description = "Données de l'exception")
            @Valid @RequestBody ExceptionRequest request) {
        Long userId = jwtUserUtil.getCurrentUserId();
        ExceptionResponse response = exceptionService.creerExceptionPourMedecin(medecinId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.EXCEPTION_CREEE));
    }

    @Operation(summary = "Modifier une exception de planning", description = "Modifie une exception de planning d'un médecin")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<ExceptionResponse>> modifierException(
            @Parameter(description = "ID de l'exception") @PathVariable Long id,
            @Valid @RequestBody ExceptionRequest request) {
        Long userId = jwtUserUtil.getCurrentUserId();
        ExceptionResponse response = exceptionService.modifierExceptionSecretaire(id, request, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, "Exception de planning mise à jour avec succès"));
    }

    @Operation(summary = "Supprimer une exception de planning", description = "Supprime une exception de planning d'un médecin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.EXCEPTION_SUPPRIMEE),
            @ApiResponse(responseCode = "404", description = MessageErreur.EXCEPTION_NON_TROUVEE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SPECIALITE)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<Void>> supprimerException(
            @Parameter(description = "ID de l'exception") @PathVariable Long id) {
        exceptionService.supprimerExceptionAdmin(id);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.EXCEPTION_SUPPRIMEE));
    }
}
