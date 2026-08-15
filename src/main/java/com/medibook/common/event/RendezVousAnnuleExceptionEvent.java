package com.medibook.common.event;

import com.medibook.rendezvous.entity.RendezVous;
import lombok.Getter;

/**
 * Événement déclenché lorsqu'un rendez-vous est annulé suite à une exception / absence du médecin.
 */
@Getter
public class RendezVousAnnuleExceptionEvent {

    private final RendezVous rendezVous;
    private final String motifException;
    private final String typeException;

    public RendezVousAnnuleExceptionEvent(RendezVous rendezVous, String motifException, String typeException) {
        this.rendezVous = rendezVous;
        this.motifException = motifException;
        this.typeException = typeException;
    }
}
