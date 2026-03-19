package com.medibook.specialite.message;

/**
 * Messages d'erreur pour les spécialités
 */
public class MessageErreur {

    private MessageErreur() {}

    public static final String UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé";
    public static final String ACCES_ADMIN_SEUL = "Accès interdit - Réservé aux administrateurs";
    public static final String PAS_DE_CABINET = "Vous n'avez pas de cabinet associé";
    public static final String PAS_ADMIN_CABINET = "Vous n'êtes pas administrateur de ce cabinet";
    public static final String SPECIALITE_DEJA_EXISTANTE = "Cette spécialité existe déjà pour ce cabinet";
    public static final String SPECIALITE_NON_TROUVEE = "Spécialité non trouvée";
    public static final String CABINET_NON_TROUVE = "Cabinet non trouvé";
}
