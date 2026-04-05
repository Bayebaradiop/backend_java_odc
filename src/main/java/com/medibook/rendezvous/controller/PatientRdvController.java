package com.medibook.rendezvous.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medibook.common.dto.PaginatedResponse;
import com.medibook.rendezvous.dto.RendezVousRequest;
import com.medibook.rendezvous.dto.RendezVousResponse;
import com.medibook.rendezvous.message.MessageSucces;
import com.medibook.rendezvous.service.RendezVousService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patient/rdv")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous - Patient", description = "API de gestion des rendez-vous pour les patients")
public class PatientRdvController {

    private final RendezVousService rendezVousService;


    /**
     * POST /api/patient/rdv - Créer un RDV
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> creerRendezVous(@Valid @RequestBody RendezVousRequest request) {
        RendezVousResponse rdv = rendezVousService.creerRendezVous(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", MessageSucces.RDV_CREE,
                "data", rdv
        ));
    }

    /**
     * GET /api/patient/rdv - Tous mes RDV
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMesRendezVous(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getMesRendezVous(PageRequest.of(page, size)));
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_LISTE,
                "data", rdvs
        ));
    }


    /**
     * GET /api/patient/rdv/en-attente - Mes RDV en attente
     */
    @GetMapping("/en-attente")
    public ResponseEntity<Map<String, Object>> getMesRendezVousEnAttente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getMesRendezVousEnAttente(PageRequest.of(page, size)));
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_LISTE,
                "data", rdvs
        ));
    }


    
    /**
     * GET /api/patient/rdv/confirmes - Mes RDV confirmés
     */
    @GetMapping("/confirmes")
    public ResponseEntity<Map<String, Object>> getMesRendezVousConfirmes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getMesRendezVousConfirmes(PageRequest.of(page, size)));
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_LISTE,
                "data", rdvs
        ));
    }


    /**
     * GET /api/patient/rdv/historique - Historique (terminés + annulés)
     */
    @GetMapping("/historique")
    public ResponseEntity<Map<String, Object>> getHistorique(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<RendezVousResponse> rdvs = PaginatedResponse.from(
                rendezVousService.getHistorique(PageRequest.of(page, size)));
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_LISTE,
                "data", rdvs
        ));
    }


    /**
     * GET /api/patient/rdv/{id} - Détails d'un RDV
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRendezVousById(@PathVariable Long id) {
        RendezVousResponse rdv = rendezVousService.getRendezVousById(id);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_DETAILS,
                "data", rdv
        ));
    }


    /**
     * PUT /api/patient/rdv/{id}/annuler - Annuler mon RDV
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annulerRendezVous(@PathVariable Long id) {
        RendezVousResponse rdv = rendezVousService.annulerRendezVous(id);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.RDV_ANNULE,
                "data", rdv
        ));
    }
}
