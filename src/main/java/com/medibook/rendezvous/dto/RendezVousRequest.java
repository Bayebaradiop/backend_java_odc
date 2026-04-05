package com.medibook.rendezvous.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RendezVousRequest {
    @NotNull(message = "Le créneau est obligatoire")
    private Long creneauId;

    @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
    private String motif;
}
