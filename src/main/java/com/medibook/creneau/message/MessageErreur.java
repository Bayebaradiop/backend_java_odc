package com.medibook.creneau.message;

public class MessageErreur {
    
    private MessageErreur() {}

    public static final String CRENEAU_NOT_FOUND = "Créneau non trouvé";
    public static final String CRENEAU_NON_DISPONIBLE = "Ce créneau n'est plus disponible";
    public static final String CRENEAU_DEJA_RESERVE = "Ce créneau est déjà réservé et ne peut pas être supprimé";
    public static final String MEDECIN_NOT_FOUND = "Médecin non trouvé ou inactif";
    public static final String HEURE_INVALIDE = "L'heure de fin doit être après l'heure de début";
    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès interdit - Réservé aux secrétaires";
}
