package com.medibook.auth.message;

public class MessageErreur {

    private MessageErreur() {}

    public static final String EMAIL_NON_TROUVE = "Aucun compte associé à cet email";
    public static final String MDP_INCORRECT = "Mot de passe incorrect";
    public static final String EMAIL_DEJA_UTILISE = "Cet email est déjà utilisé";
    public static final String TELEPHONE_DEJA_UTILISE = "Ce numéro de téléphone est déjà utilisé";
    public static final String COMPTE_INACTIF = "Votre compte est inactif ou suspendu. Contactez l'administrateur pour le réactiver";
    public static final String CODE_INVALIDE = "Le code de réinitialisation saisi est invalide";
    public static final String CODE_EXPIRE = "Le code de réinitialisation a expiré. Veuillez en demander un nouveau";
    public static final String ERREUR_ENVOI_EMAIL = "Erreur lors de l'envoi de l'email de réinitialisation";
}
