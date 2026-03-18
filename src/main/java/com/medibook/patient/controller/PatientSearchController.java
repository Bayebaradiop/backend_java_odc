package com.medibook.patient.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medibook.cabinet.dto.CabinetResponse;
import com.medibook.patient.message.MessageSucces;
import com.medibook.patient.service.PatientSearchService;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.user.dto.MedecinResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@Tag(name = "Recherche - Patient", description = "API de recherche de cabinets, spécialités et médecins pour les patients")
public class PatientSearchController {

    private final PatientSearchService patientSearchService;

    // ==================== CABINETS ====================

    /**
     * GET /api/patient/cabinets - Lister tous les cabinets actifs
     */
    @GetMapping("/cabinets")
    public ResponseEntity<Map<String, Object>> getAllCabinets() {
        List<CabinetResponse> cabinets = patientSearchService.getAllCabinets();
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.CABINETS_LISTE,
                "data", cabinets
        ));
    }

    /**
     * GET /api/patient/cabinets/{id} - Voir un cabinet
     */
    @GetMapping("/cabinets/{id}")
    public ResponseEntity<Map<String, Object>> getCabinetById(@PathVariable Long id) {
        CabinetResponse cabinet = patientSearchService.getCabinetById(id);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.CABINET_DETAILS,
                "data", cabinet
        ));
    }

    // ==================== SPÉCIALITÉS ====================

    /**
     * GET /api/patient/specialites - Toutes les spécialités
     */
    @GetMapping("/specialites")
    public ResponseEntity<Map<String, Object>> getAllSpecialites() {
        List<SpecialiteResponse> specialites = patientSearchService.getAllSpecialites();
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.SPECIALITES_LISTE,
                "data", specialites
        ));
    }

    /**
     * GET /api/patient/specialites/cabinet/{id} - Spécialités d'un cabinet
     */
    @GetMapping("/specialites/cabinet/{id}")
    public ResponseEntity<Map<String, Object>> getSpecialitesByCabinet(@PathVariable Long id) {
        List<SpecialiteResponse> specialites = patientSearchService.getSpecialitesByCabinet(id);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.SPECIALITES_LISTE,
                "data", specialites
        ));
    }

    // ==================== MÉDECINS ====================

    /**
     * GET /api/patient/medecins - Tous les médecins actifs
     * Filtres optionnels: ?specialite_id=X&cabinet_id=Y
     */
    @GetMapping("/medecins")
    public ResponseEntity<Map<String, Object>> getMedecins(
            @RequestParam(name = "specialite_id", required = false) Long specialiteId,
            @RequestParam(name = "cabinet_id", required = false) Long cabinetId) {
        
        List<MedecinResponse> medecins = patientSearchService.getMedecins(specialiteId, cabinetId);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.MEDECINS_LISTE,
                "data", medecins
        ));
    }

    /**
     * GET /api/patient/medecins/{id} - Détails d'un médecin
     */
    @GetMapping("/medecins/{id}")
    public ResponseEntity<Map<String, Object>> getMedecinById(@PathVariable Long id) {
        MedecinResponse medecin = patientSearchService.getMedecinById(id);
        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.MEDECIN_DETAILS,
                "data", medecin
        ));
    }
}
