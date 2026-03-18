package com.medibook.ExceptionsPlanning.message;

/**
 * Messages d'erreur pour le module ExceptionsPlanning
 */
public class MessageErreur {

    private MessageErreur() {}

    public static final String MEDECIN_NON_TROUVE = "Médecin non trouvé";
    public static final String EXCEPTION_NON_TROUVEE = "Exception de planning non trouvée";
    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_MEDECIN_SEUL = "Accès interdit - Réservé aux médecins";
    public static final String ACCES_REFUSE = "Accès refusé - Vous ne pouvez pas modifier cette exception";
    public static final String DONNEES_INVALIDES = "Données invalides";
    public static final String HEURE_INVALIDE = "Les heures de début et fin sont invalides";
    public static final String ACCES_SECRETAIRE_SPECIALITE = "Accès interdit - Réservé au secrétaire de la même spécialité";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès réservé aux secrétaires";
    public static final String SPECIALITE_SECRETAIRE_OBLIGATOIRE = "Le/la secrétaire doit avoir une spécialité";
    public static final String MEDECIN_MEME_CABINET = "Le médecin doit être dans le même cabinet";
    public static final String MEDECIN_MEME_SPECIALITE = "Vous ne pouvez créer l'exception que pour un médecin de votre spécialité";
}
