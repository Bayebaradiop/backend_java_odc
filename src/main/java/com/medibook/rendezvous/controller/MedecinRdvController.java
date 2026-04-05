package com.medibook.rendezvous.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.dto.PaginatedResponse;
import com.medibook.rendezvous.dto.RendezVousResponse;
import com.medibook.rendezvous.message.MessageErreur;
import com.medibook.rendezvous.message.MessageSucces;
import com.medibook.rendezvous.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecin/rdv")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous - Médecin", description = "API de gestion des RDV pour les médecins")
public class MedecinRdvController {

    private final RendezVousService rendezVousService;

    @Operation(summary = "Mes rendez-vous", description = "Liste tous les RDV du médecin connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_LISTE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<RendezVousResponse>>> getMesRdv(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getRdvMedecin(PageRequest.of(page, size)));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(rdvs, MessageSucces.RDV_LISTE));
    }

    @Operation(summary = "RDV en attente", description = "Liste les RDV en attente de confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_LISTE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<RendezVousResponse>>> getRdvEnAttente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getRdvMedecinEnAttente(PageRequest.of(page, size)));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(rdvs, MessageSucces.RDV_LISTE));
    }

    @Operation(summary = "Confirmer un RDV", description = "Confirme un rendez-vous en attente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_CONFIRME),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.RDV_NOT_FOUND)
    })
    @PatchMapping("/{id}/confirmer")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<RendezVousResponse>> confirmerRdv(@PathVariable Long id) {
        RendezVousResponse rdv = rendezVousService.confirmerRdv(id);
        return ResponseEntity.ok(ApiStandardResponse.success(rdv, MessageSucces.RDV_CONFIRME));
    }

    @Operation(summary = "Terminer un RDV", description = "Marque un rendez-vous confirmé comme terminé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_TERMINE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.RDV_NOT_FOUND)
    })
    @PatchMapping("/{id}/terminer")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<RendezVousResponse>> terminerRdv(@PathVariable Long id) {
        RendezVousResponse rdv = rendezVousService.terminerRdv(id);
        return ResponseEntity.ok(ApiStandardResponse.success(rdv, MessageSucces.RDV_TERMINE));
    }
}
