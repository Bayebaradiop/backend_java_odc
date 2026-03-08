## 📡 ENDPOINTS API - MEDIBOOK SaaS

┌────────────────────────────────────────────────────────────────────────────┐
│  🔴 SUPER_ADMIN ENDPOINTS                                                  │
│  Mission: Gérer la plateforme et les cabinets                              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  🔐 Authentification                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (POST)   /api/auth/login                         (Connexion)              │
│  (POST)   /api/auth/logout                        (Déconnexion)            │
│  (GET)    /api/auth/profile                       (Mon profil)             │
│  (PUT)    /api/auth/profile                       (Modifier mon profil)    │
│                                                                            │
│  🏥 Gestion des Cabinets                                                   │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/super-admin/cabinets               (Lister les cabinets)    │
│  (POST)   /api/super-admin/cabinets               (Créer un cabinet)       │
│  (GET)    /api/super-admin/cabinets/{id}          (Voir un cabinet)        │
│  (PUT)    /api/super-admin/cabinets/{id}          (Modifier un cabinet)    │
│  (DELETE) /api/super-admin/cabinets/{id}          (Supprimer un cabinet)   │
│  (PATCH)  /api/super-admin/cabinets/{id}/status   (Activer/Désactiver)     │
│                                                                            │
│  👥 Gestion des Admins                                                     │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/super-admin/admins                 (Lister les admins)      │
│  (POST)   /api/super-admin/admins                 (Créer un admin)         │
│  (GET)    /api/super-admin/admins/{id}            (Voir un admin)          │
│  (PUT)    /api/super-admin/admins/{id}            (Modifier un admin)      │
│  (DELETE) /api/super-admin/admins/{id}            (Supprimer un admin)     │
│  (PATCH)  /api/super-admin/admins/{id}/status     (Bloquer/Débloquer)      │
│                                                                            │
│  👁️ Vue globale                                                            │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/super-admin/users                  (Tous les utilisateurs)  │
│  (PATCH)  /api/super-admin/users/{id}/status      (Bloquer/Débloquer)      │
│                                                                            │
│  📊 Statistiques                                                           │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/super-admin/stats                  (Statistiques globales)  │
│  (GET)    /api/super-admin/stats/cabinets         (Stats par cabinet)      │
│  (GET)    /api/super-admin/stats/rdv              (Stats des RDV)          │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────────────────────┐
│  🟠 ADMIN ENDPOINTS                                                        │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  🔐 Authentification                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (POST)   /api/auth/login                         (Connexion)              │
│  (POST)   /api/auth/logout                        (Déconnexion)            │
│  (GET)    /api/auth/profile                       (Mon profil)             │
│  (PUT)    /api/auth/profile                       (Modifier mon profil)    │
│                                                                            │
│  🏥 Mon Cabinet                                                            │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/cabinet                      (Voir mon cabinet)       │
│  (PUT)    /api/admin/cabinet                      (Modifier mon cabinet)   │
│                                                                            │
│  🩺 Gestion des Spécialités du Cabinet                                     │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/specialites                  (Lister les spécialités) │
│  (POST)   /api/admin/specialites                  (Créer une spécialité)   │
│  (GET)    /api/admin/specialites/{id}             (Voir une spécialité)    │
│  (PUT)    /api/admin/specialites/{id}             (Modifier une spécialité)│
│  (DELETE) /api/admin/specialites/{id}             (Supprimer une spécial.) │
│                                                                            │
│  👨‍⚕️ Gestion des Médecins                                                   │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/medecins                     (Lister mes médecins)    │
│  (POST)   /api/admin/medecins                     (Recruter un médecin)    │
│  (GET)    /api/admin/medecins/{id}                (Voir un médecin)        │
│  (PUT)    /api/admin/medecins/{id}                (Modifier un médecin)    │
│  (DELETE) /api/admin/medecins/{id}                (Supprimer un médecin)   │
│  (PATCH)  /api/admin/medecins/{id}/status         (Bloquer/Débloquer)      │
│                                                                            │
│  👩‍💼 Gestion des Secrétaires                                                │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/secretaires                  (Lister mes secrétaires) │
│  (POST)   /api/admin/secretaires                  (Recruter secrétaire)    │
│  (GET)    /api/admin/secretaires/{id}             (Voir une secrétaire)    │
│  (PUT)    /api/admin/secretaires/{id}             (Modifier secrétaire)    │
│  (DELETE) /api/admin/secretaires/{id}             (Supprimer secrétaire)   │
│  (PATCH)  /api/admin/secretaires/{id}/status      (Bloquer/Débloquer)      │
│                                                                            │
│  📊 Statistiques                                                           │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/stats                        (Stats du cabinet)       │
│  (GET)    /api/admin/stats/rdv                    (Stats des RDV)          │
│  (GET)    /api/admin/stats/medecins               (Stats par médecin)      │
│                                                                            │
│  👁️ Vue d'ensemble (lecture seule)                                         │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/admin/plannings                    (Tous les plannings)     │
│  (GET)    /api/admin/rdv                          (Tous les RDV)           │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────────────────────┐
│  🟢 SECRETAIRE ENDPOINTS                                                   │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  🔐 Authentification                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (POST)   /api/auth/login                         (Connexion)              │
│  (POST)   /api/auth/logout                        (Déconnexion)            │
│  (GET)    /api/auth/profile                       (Mon profil)             │
│  (PUT)    /api/auth/profile                       (Modifier mon profil)    │
│                                                                            │
│  👨‍⚕️ Médecins du cabinet (lecture)                                          │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/medecins                (Lister les médecins)    │
│  (GET)    /api/secretaire/medecins/{id}           (Voir un médecin)        │
│                                                                            │
│  🩺 Spécialités du cabinet (lecture)                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/specialites             (Lister les spécialités) │
│                                                                            │
│  📅 Gestion des Plannings                                                  │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/plannings               (Tous les plannings)     │
│  (POST)   /api/secretaire/plannings               (Créer un planning)      │
│  (GET)    /api/secretaire/plannings/{id}          (Voir un planning)       │
│  (PUT)    /api/secretaire/plannings/{id}          (Modifier un planning)   │
│  (DELETE) /api/secretaire/plannings/{id}          (Supprimer un planning)  │
│  (GET)    /api/secretaire/plannings/medecin/{id}  (Plannings d'un médecin) │
│                                                                            │
│  🕐 Gestion des Créneaux                                                   │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/creneaux                (Tous les créneaux)      │
│  (POST)   /api/secretaire/creneaux                (Créer un créneau)       │
│  (PUT)    /api/secretaire/creneaux/{id}           (Modifier un créneau)    │
│  (DELETE) /api/secretaire/creneaux/{id}           (Supprimer un créneau)   │
│  (GET)    /api/secretaire/creneaux/planning/{id}  (Créneaux d'un planning) │
│                                                                            │
│  📋 Gestion des Rendez-vous                                                │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/rdv                     (Tous les RDV)           │
│  (GET)    /api/secretaire/rdv/en-attente          (RDV en attente)         │
│  (GET)    /api/secretaire/rdv/confirmes           (RDV confirmés)          │
│  (GET)    /api/secretaire/rdv/{id}                (Voir un RDV)            │
│  (POST)   /api/secretaire/rdv                     (Prendre RDV pour patient)│
│  (PATCH)  /api/secretaire/rdv/{id}/valider        (Valider un RDV)         │
│  (PATCH)  /api/secretaire/rdv/{id}/refuser        (Refuser un RDV)         │
│  (PATCH)  /api/secretaire/rdv/{id}/annuler        (Annuler un RDV)         │
│  (GET)    /api/secretaire/rdv/medecin/{id}        (RDV d'un médecin)       │
│                                                                            │
│  👥 Patients                                                               │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/secretaire/patients                (Lister les patients)    │
│  (GET)    /api/secretaire/patients/{id}           (Voir un patient)        │
│  (POST)   /api/secretaire/patients                (Inscrire un patient)    │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────────────────────┐
│  🔵 MEDECIN ENDPOINTS                                                      │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  🔐 Authentification                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (POST)   /api/auth/login                         (Connexion)              │
│  (POST)   /api/auth/logout                        (Déconnexion)            │
│  (GET)    /api/auth/profile                       (Mon profil)             │
│  (PUT)    /api/auth/profile                       (Modifier mon profil)    │
│                                                                            │
│  📅 Mon Planning (lecture seule)                                           │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/medecin/planning                   (Mon planning complet)   │
│  (GET)    /api/medecin/planning/today             (Planning du jour)       │
│  (GET)    /api/medecin/planning/semaine           (Planning de la semaine) │
│  (GET)    /api/medecin/planning/{date}            (Planning d'une date)    │
│                                                                            │
│  📋 Mes Rendez-vous (lecture seule)                                        │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/medecin/rdv                        (Tous mes RDV)           │
│  (GET)    /api/medecin/rdv/confirmes              (Mes RDV confirmés)      │
│  (GET)    /api/medecin/rdv/today                  (RDV du jour)            │
│  (GET)    /api/medecin/rdv/{id}                   (Voir un RDV)            │
│                                                                            │
│  👥 Mes Patients                                                           │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/medecin/patients                   (Mes patients)           │
│  (GET)    /api/medecin/patients/{id}              (Infos d'un patient)     │
│                                                                            │
│  📊 Mes Statistiques                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/medecin/stats                      (Mes statistiques)       │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────────────────────┐
│  🟡 PATIENT ENDPOINTS                                                      │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  🔐 Authentification                                                       │
│  ─────────────────────────────────────────────────────────────────────     │
│  (POST)   /api/auth/register                      (Inscription)            │
│  (POST)   /api/auth/login                         (Connexion)              │
│  (POST)   /api/auth/logout                        (Déconnexion)            │
│  (GET)    /api/auth/profile                       (Mon profil)             │
│  (PUT)    /api/auth/profile                       (Modifier mon profil)    │
│  (POST)   /api/auth/forgot-password               (Mot de passe oublié)    │
│  (POST)   /api/auth/reset-password                (Réinitialiser MDP)      │
│                                                                            │
│  🔍 Recherche                                                              │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/patient/cabinets                   (Lister les cabinets)    │
│  (GET)    /api/patient/cabinets/{id}              (Voir un cabinet)        │
│  (GET)    /api/patient/specialites                (Toutes les spécialités) │
│  (GET)    /api/patient/specialites/cabinet/{id}   (Spécialités d'un cab.)  │
│  (GET)    /api/patient/medecins                   (Tous les médecins)      │
│  (GET)    /api/patient/medecins?specialite_id={id} (Par spécialité)        │
│  (GET)    /api/patient/medecins?cabinet_id={id}    (Par cabinet)           │
│  (GET)    /api/patient/medecins/{id}              (Détails d'un médecin)   │
│                                                                            │
│  📅 Disponibilités                                                         │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/patient/medecins/{id}/disponibilites        (Créneaux dispo)│
│  (GET)    /api/patient/medecins/{id}/disponibilites?date={date} (Par date) │
│                                                                            │
│  📋 Mes Rendez-vous                                                        │
│  ─────────────────────────────────────────────────────────────────────     │
│  (GET)    /api/patient/rdv                        (Mes RDV)                │
│  (GET)    /api/patient/rdv/en-attente             (Mes RDV en attente)     │
│  (GET)    /api/patient/rdv/confirmes              (Mes RDV confirmés)      │
│  (GET)    /api/patient/rdv/historique             (Historique des RDV)     │
│  (GET)    /api/patient/rdv/{id}                   (Voir un RDV)            │
│  (POST)   /api/patient/rdv                        (Demander un RDV)        │
│  (PATCH)  /api/patient/rdv/{id}/annuler           (Annuler mon RDV)        │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘