package com.medibook.stats.message;

public class MessageErreur {

    private MessageErreur() {}

    public static final String NON_AUTHENTIFIE = "Non authentifié";
    public static final String ACCES_MEDECIN_SEUL = "Accès interdit - Réservé aux médecins";
    public static final String ACCES_ADMIN_SEUL = "Accès interdit - Réservé aux administrateurs";
    public static final String ACCES_SUPER_ADMIN_SEUL = "Accès interdit - Réservé au Super Admin";
    public static final String CABINET_NON_TROUVE = "Cabinet non trouvé";
    public static final String ACCES_SECRETAIRE_SEUL = "Accès interdit - Réservé aux secrétaires";
}
