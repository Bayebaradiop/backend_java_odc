package com.medibook.planning.message;

/**
 * Messages d'erreur pour le module planning
 */
public class MessageErreur {

    private MessageErreur() {}

    // Erreurs utilisateur
    public static final String UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé";
    public static final String MEDECIN_NON_TROUVE = "Médecin non trouvé";
    public static final String PLANNING_NON_TROUVE = "Planning non trouvé";
    public static final String PLANNING_EXISTANT = "Un planning existe déjà pour ce médecin à ce jour";
    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_MEDECIN_SEUL = "Accès interdit - Réservé aux médecins";
    public static final String ACCES_SECRETAIRE_SPECIALITE = "Accès interdit - Réservé au secrétaire de la même spécialité";

    // Erreurs secrétaire
    public static final String ACCES_SECRETAIRE_SEUL = "Accès réservé aux secrétaires";
    public static final String SPECIALITE_SECRETAIRE_OBLIGATOIRE = "Le/la secrétaire doit avoir une spécialité assignée";
    public static final String MEDECIN_MEME_CABINET = "Le médecin doit être dans le même cabinet que le secrétaire";
    public static final String MEDECIN_MEME_SPECIALITE = "Vous ne pouvez créer le planning que pour un médecin de votre spécialité";

    // Erreurs créneau
    public static final String CRENEAU_INDISPONIBLE = "Créneau non disponible";
    public static final String HEURE_INVALIDE = "L'heure de fin doit être après l'heure de début";
}
