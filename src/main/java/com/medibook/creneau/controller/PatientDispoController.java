package com.medibook.creneau.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medibook.creneau.dto.CreneauResponse;
import com.medibook.creneau.message.MessageSucces;
import com.medibook.creneau.service.CreneauService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientDispoController {

    private final CreneauService creneauService;

    
     //GET /api/patient/medecins/{id}/disponibilites
   
    @GetMapping("/medecins/{id}/disponibilites")
    public ResponseEntity<Map<String, Object>> getDisponibilites(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<CreneauResponse> creneaux = creneauService.getDisponibilitesMedecin(id, date);

        return ResponseEntity.ok(Map.of(
                "message", MessageSucces.CRENEAUX_DISPONIBLES,
                "data", creneaux,
                "count", creneaux.size()
        ));
    }
}
