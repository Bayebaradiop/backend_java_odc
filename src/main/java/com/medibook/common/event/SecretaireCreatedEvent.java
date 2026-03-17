package com.medibook.common.event;

import com.medibook.user.entity.Utilisateur;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Événement émis lors de la création d'un(e) secrétaire
 */
@Getter
public class SecretaireCreatedEvent extends ApplicationEvent {

    private final Utilisateur secretaire;
    private final String motDePasseTemporaire;
    private final String cabinetNom;

    public SecretaireCreatedEvent(Object source, Utilisateur secretaire, String motDePasseTemporaire, String cabinetNom) {
        super(source);
        this.secretaire = secretaire;
        this.motDePasseTemporaire = motDePasseTemporaire;
        this.cabinetNom = cabinetNom;
    }
}
