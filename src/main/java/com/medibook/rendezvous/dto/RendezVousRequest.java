package com.medibook.rendezvous.dto;

import lombok.Data;

@Data
public class RendezVousRequest {
    private Long creneauId;   // ID du créneau choisi
    private String motif;     // Raison du RDV (optionnel)
}
