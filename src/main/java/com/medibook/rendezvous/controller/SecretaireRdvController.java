package com.medibook.rendezvous.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.dto.PaginatedResponse;
import com.medibook.common.enums.StatutRdv;
import com.medibook.common.security.SecurityService;
import com.medibook.rendezvous.dto.RendezVousResponse;
import com.medibook.rendezvous.message.MessageErreur;
import com.medibook.rendezvous.message.MessageSucces;
import com.medibook.rendezvous.service.RendezVousService;
import com.medibook.user.entity.Utilisateur;
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
@RequestMapping("/api/secretaire/rdv")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous - Secrétaire", description = "API de gestion des RDV pour les secrétaires")
public class SecretaireRdvController {

    private final RendezVousService rendezVousService;
    private final SecurityService securityService;

    @Operation(summary = "Tous les RDV du cabinet", description = "Liste tous les RDV du cabinet de la secrétaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_LISTE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<RendezVousResponse>>> getRdvCabinet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long cabinetId = getCabinetId();
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getRdvCabinet(cabinetId, PageRequest.of(page, size)));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(rdvs, MessageSucces.RDV_LISTE));
    }

    @Operation(summary = "RDV en attente du cabinet", description = "Liste les RDV en attente de confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_LISTE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SEUL)
    })
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<RendezVousResponse>>> getRdvEnAttente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long cabinetId = getCabinetId();
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getRdvCabinetParStatut(cabinetId, StatutRdv.EN_ATTENTE, PageRequest.of(page, size)));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(rdvs, MessageSucces.RDV_LISTE));
    }

    @Operation(summary = "Confirmer un RDV", description = "Confirme un rendez-vous en attente du cabinet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_CONFIRME),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.RDV_NOT_FOUND)
    })
    @PatchMapping("/{id}/confirmer")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<RendezVousResponse>> confirmerRdv(@PathVariable Long id) {
        Long cabinetId = getCabinetId();
        RendezVousResponse rdv = rendezVousService.confirmerRdvParSecretaire(id, cabinetId);
        return ResponseEntity.ok(ApiStandardResponse.success(rdv, MessageSucces.RDV_CONFIRME));
    }

    @Operation(summary = "Annuler un RDV", description = "Annule un rendez-vous du cabinet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.RDV_ANNULE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.RDV_NOT_FOUND)
    })
    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<RendezVousResponse>> annulerRdv(@PathVariable Long id) {
        Long cabinetId = getCabinetId();
        RendezVousResponse rdv = rendezVousService.annulerRdvParSecretaire(id, cabinetId);
        return ResponseEntity.ok(ApiStandardResponse.success(rdv, MessageSucces.RDV_ANNULE));
    }

    private Long getCabinetId() {
        Utilisateur secretaire = securityService.getUtilisateurConnecte();
        return secretaire.getCabinet().getId();
    }
}
