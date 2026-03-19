package com.medibook.rendezvous.message;

public class MessageErreur {

    private MessageErreur() {}

    public static final String RDV_NOT_FOUND = "Rendez-vous non trouvé";
    public static final String CRENEAU_NON_DISPONIBLE = "Ce créneau n'est plus disponible. Un autre patient l'a peut-être réservé";
    public static final String MEDECIN_INDISPONIBLE_CRENEAU = "Le médecin est indisponible sur ce créneau (indisponibilité déclarée)";
    public static final String RDV_DEJA_ANNULE = "Ce rendez-vous est déjà annulé";
    public static final String ANNULATION_NON_AUTORISEE = "Vous ne pouvez annuler que vos propres rendez-vous";
    public static final String CRENEAU_NOT_FOUND = "Créneau non trouvé";
    public static final String PATIENT_NOT_FOUND = "Patient non trouvé";
    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_MEDECIN_SEUL = "Accès interdit - Réservé aux médecins";
    public static final String CONFIRMATION_IMPOSSIBLE = "Le rendez-vous doit être en attente pour pouvoir être confirmé";
    public static final String TERMINAISON_IMPOSSIBLE = "Le rendez-vous doit être confirmé pour pouvoir être terminé";
    public static final String RDV_NON_ASSIGNE_MEDECIN = "Ce rendez-vous ne vous est pas assigné";
    public static final String RDV_NON_ASSIGNE_CABINET = "Ce rendez-vous n'appartient pas à votre cabinet";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès interdit - Réservé aux secrétaires";
}
