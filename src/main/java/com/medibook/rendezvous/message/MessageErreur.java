package com.medibook.rendezvous.message;

public class MessageErreur {
    public static final String RDV_NOT_FOUND = "Rendez-vous non trouvé";
    public static final String CRENEAU_NON_DISPONIBLE = "Ce créneau n'est plus disponible";
    public static final String RDV_DEJA_ANNULE = "Ce rendez-vous est déjà annulé";
    public static final String ANNULATION_IMPOSSIBLE = "Impossible d'annuler ce rendez-vous";
    public static final String CRENEAU_NOT_FOUND = "Créneau non trouvé";
    public static final String PATIENT_NOT_FOUND = "Patient non trouvé";
    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_MEDECIN_SEUL = "Accès interdit - Réservé aux médecins";
    public static final String CONFIRMATION_IMPOSSIBLE = "Impossible de confirmer ce rendez-vous";
    public static final String TERMINAISON_IMPOSSIBLE = "Impossible de terminer ce rendez-vous";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès interdit - Réservé aux secrétaires";
    
    private MessageErreur() {}
}
