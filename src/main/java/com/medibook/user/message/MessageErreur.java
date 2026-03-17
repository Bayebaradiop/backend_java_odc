package com.medibook.user.message;

/**
 * Messages d'erreur pour le module utilisateur
 */
public class MessageErreur {

    private MessageErreur() {}

    // Erreurs génériques
    public static final String UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé";
    public static final String EMAIL_DEJA_UTILISE = "Cet email est déjà utilisé";
    public static final String TELEPHONE_DEJA_UTILISE = "Ce téléphone est déjà utilisé";
    public static final String ACCES_INTERDIT = "Accès interdit";

    // Erreurs médecin
    public static final String MEDECIN_NON_TROUVE = "Médecin non trouvé";
    public static final String MEDECIN_DEJA_EXISTANT = "Ce médecin existe déjà";
    public static final String SPECIALITE_OBLIGATOIRE = "La spécialité est obligatoire";

    // Erreurs secretaire
    public static final String SECRETAIRE_NON_TROUVE = "Secrétaire non trouvé";
    public static final String SECRETAIRE_DEJA_EXISTANT = "Ce/Cette secretary existe déjà";
}
