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
    public static final String ACCES_ADMIN_SEUL = "Accès interdit - Réservé aux administrateurs";
    public static final String PAS_DE_CABINET = "Vous n'avez pas de cabinet associé";

    // Erreurs médecin
    public static final String MEDECIN_NON_TROUVE = "Médecin non trouvé";
    public static final String MEDECIN_DEJA_EXISTANT = "Ce médecin existe déjà";
    public static final String SPECIALITE_OBLIGATOIRE = "La spécialité est obligatoire";
    public static final String EMAIL_DEJA_UTILISE_CABINET = "Cet email est déjà utilisé par un utilisateur de ce cabinet";
    public static final String TELEPHONE_DEJA_UTILISE_CABINET = "Ce téléphone est déjà utilisé par un utilisateur de ce cabinet";
    public static final String SPECIALITE_NON_TROUVEE = "Spécialité non trouvée";
    public static final String SPECIALITE_HORS_CABINET = "La spécialité n'appartient pas à votre cabinet";
    public static final String MEDECIN_HORS_CABINET = "Ce médecin n'appartient pas à votre cabinet";

    // Erreurs secrétaire
    public static final String SECRETAIRE_NON_TROUVE = "Secrétaire non trouvé(e)";
    public static final String SECRETAIRE_DEJA_EXISTANT = "Ce/Cette secrétaire existe déjà";
    public static final String SECRETAIRE_HORS_CABINET = "Ce/Cette secrétaire n'appartient pas à votre cabinet";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès réservé aux secrétaires";
    public static final String SECRETAIRE_SANS_CABINET_OU_SPECIALITE = "Le/la secrétaire doit appartenir à un cabinet";
}
