package com.medibook.creneau.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.creneau.dto.CreneauResponse;
import com.medibook.creneau.message.MessageErreur;
import com.medibook.creneau.message.MessageSucces;
import com.medibook.creneau.service.CreneauService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secretaire/creneaux")
@RequiredArgsConstructor
@Tag(name = "Créneaux - Secrétaire", description = "Consultation et gestion des créneaux (générés automatiquement à partir des plannings)")
public class SecretaireCreneauController {

    private final CreneauService creneauService;

    @Operation(summary = "Créneaux d'un médecin", description = "Liste tous les créneaux d'un médecin du cabinet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.CRENEAUX_LISTE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SEUL)
    })
    @GetMapping("/medecin/{medecinId}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<List<CreneauResponse>>> getCreneauxMedecin(@PathVariable Long medecinId) {
        List<CreneauResponse> creneaux = creneauService.getCreneauxMedecin(medecinId);
        return ResponseEntity.ok(ApiStandardResponse.success(creneaux, MessageSucces.CRENEAUX_LISTE));
    }

    @Operation(summary = "Supprimer un créneau", description = "Supprime un créneau disponible (non réservé)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.CRENEAU_SUPPRIME),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.CRENEAU_NOT_FOUND)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<Void>> supprimerCreneau(@PathVariable Long id) {
        creneauService.supprimerCreneau(id);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.CRENEAU_SUPPRIME));
    }
}
