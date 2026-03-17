package com.medibook.planning.message;

/**
 * Messages d'erreur pour le module planning
 */
public class MessageErreur {

    private MessageErreur() {}

    // Erreurs génériques
    public static final String UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé";
    public static final String MEDECIN_NON_TROUVE = "Médecin non trouvé";
    public static final String PLANNING_NON_TROUVE = "Planning non trouvé";
    public static final String PLANNING_EXISTANT = "Un planning existe déjà pour ce jour";

    // Erreurs secretary
    public static final String ACCES_SECRETAIRE_SEUL = "Accès réservé aux secrétaires";
    public static final String SPECIALITE_SECRETAIRE_OBLIGATOIRE = "Le/la secrétaire doit avoir une spécialité";
    public static final String MEDECIN_MEME_CABINET = "Le médecin doit être dans le même cabinet";
    public static final String MEDECIN_MEME_SPECIALITE = "Vous ne pouvez créer le planning que pour un médecin de votre spécialité";

    // Erreurs créneau
    public static final String CRENEAU_INDISPONIBLE = "Créneau non disponible";
    public static final String HEURE_INVALIDE = "Les heures de début et fin sont invalides";
}
