package com.medibook.cabinet.message;

/**
 * Messages d'erreur pour le module Cabinet
 */
public final class MessageErreur {

    private MessageErreur() {
        // Classe utilitaire
    }

    public static final String CABINET_NON_TROUVE = "Cabinet non trouvé";
    public static final String CABINET_DEJA_EXISTANT = "Un cabinet avec ce nom existe déjà";
    public static final String EMAIL_DEJA_UTILISE = "Cet email est déjà utilisé par un autre cabinet";
    public static final String TELEPHONE_DEJA_UTILISE = "Ce numéro de téléphone est déjà utilisé";
    public static final String ERREUR_UPLOAD_LOGO = "Erreur lors de l'upload du logo";
    public static final String ERREUR_SUPPRESSION_LOGO = "Erreur lors de la suppression du logo";
    public static final String ACCES_INTERDIT = "Vous n'avez pas l'autorisation d'effectuer cette action";
    public static final String SEULEMENT_SUPER_ADMIN = "Seul le Super Admin peut créer un cabinet";
    public static final String ADMIN_INFO_INCOMPLETE = "Les informations de l'administrateur sont incomplètes";
    public static final String ADMIN_EMAIL_DEJA_UTILISE = "L'email de l'administrateur est déjà utilisé";
    public static final String ADMIN_TELEPHONE_DEJA_UTILISE = "Le téléphone de l'administrateur est déjà utilisé";
}
