package com.medibook.common.event;

import com.medibook.user.entity.Utilisateur;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Événement émis lors de la création d'un médecin
 */
@Getter
public class MedecinCreatedEvent extends ApplicationEvent {

    private final Utilisateur medecin;
    private final String motDePasseTemporaire;
    private final String cabinetNom;

    public MedecinCreatedEvent(Object source, Utilisateur medecin, String motDePasseTemporaire, String cabinetNom) {
        super(source);
        this.medecin = medecin;
        this.motDePasseTemporaire = motDePasseTemporaire;
        this.cabinetNom = cabinetNom;
    }
}
