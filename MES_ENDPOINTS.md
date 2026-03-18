# Mes Endpoints — MEDIBOOK API

> 63 endpoints | Base URL: `http://localhost:8085`

---

## Authentification (Public)

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| POST | /api/auth/register | Inscription patient | Public |
| POST | /api/auth/login | Connexion | Public |
| POST | /api/auth/logout | Déconnexion | Public |
| GET | /api/auth/me | Mon profil | Authentifié |
| PUT | /api/auth/me | Modifier mon profil | Authentifié |
| POST | /api/auth/forgot-password | Demander reset mot de passe | Public |
| POST | /api/auth/reset-password | Reset mot de passe | Public |

---

## Super Admin

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| POST | /api/super-admin/cabinets | Créer un cabinet | SUPER_ADMIN |
| GET | /api/super-admin/cabinets | Lister les cabinets | SUPER_ADMIN |
| GET | /api/super-admin/cabinets/{id} | Voir un cabinet | SUPER_ADMIN |
| PUT | /api/super-admin/cabinets/{id} | Modifier un cabinet | SUPER_ADMIN |
| DELETE | /api/super-admin/cabinets/{id} | Supprimer un cabinet | SUPER_ADMIN |
| PATCH | /api/super-admin/cabinets/{id}/toggle-status | Activer/Désactiver | SUPER_ADMIN |
| PATCH | /api/super-admin/cabinets/{id}/logo | Changer le logo | SUPER_ADMIN |
| GET | /api/super-admin/dashboard | Dashboard plateforme | SUPER_ADMIN |
| GET | /api/super-admin/stats | Statistiques globales | SUPER_ADMIN |

---

## Admin

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| POST | /api/admin/specialites | Créer une spécialité | ADMIN |
| GET | /api/admin/specialites | Lister les spécialités | ADMIN |
| GET | /api/admin/specialites/{id} | Voir une spécialité | ADMIN |
| PUT | /api/admin/specialites/{id} | Modifier une spécialité | ADMIN |
| DELETE | /api/admin/specialites/{id} | Supprimer une spécialité | ADMIN |
| POST | /api/admin/medecins | Créer un médecin | ADMIN |
| GET | /api/admin/medecins | Lister les médecins | ADMIN |
| GET | /api/admin/medecins/{id} | Voir un médecin | ADMIN |
| PUT | /api/admin/medecins/{id} | Modifier un médecin | ADMIN |
| DELETE | /api/admin/medecins/{id} | Supprimer un médecin | ADMIN |
| PATCH | /api/admin/medecins/{id}/status | Bloquer/Débloquer médecin | ADMIN |
| POST | /api/admin/secretaires | Créer une secrétaire | ADMIN |
| GET | /api/admin/secretaires | Lister les secrétaires | ADMIN |
| GET | /api/admin/secretaires/{id} | Voir une secrétaire | ADMIN |
| PUT | /api/admin/secretaires/{id} | Modifier une secrétaire | ADMIN |
| DELETE | /api/admin/secretaires/{id} | Supprimer une secrétaire | ADMIN |
| PATCH | /api/admin/secretaires/{id}/status | Bloquer/Débloquer secrétaire | ADMIN |
| GET | /api/admin/stats | Statistiques du cabinet | ADMIN |

---

## Secrétaire

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | /api/secretaire/medecins | Médecins de même spécialité | SECRETAIRE |
| GET | /api/secretaire/planning/medecin/{medecinId} | Plannings d'un médecin | SECRETAIRE |
| POST | /api/secretaire/planning | Créer un planning | SECRETAIRE |
| GET | /api/secretaire/creneaux/medecin/{medecinId} | Créneaux d'un médecin | SECRETAIRE |
| DELETE | /api/secretaire/creneaux/{id} | Supprimer un créneau | SECRETAIRE |
| GET | /api/secretaire/rdv | Tous les RDV du cabinet | SECRETAIRE |
| GET | /api/secretaire/rdv/en-attente | RDV en attente | SECRETAIRE |
| PATCH | /api/secretaire/rdv/{id}/confirmer | Confirmer un RDV | SECRETAIRE |
| PATCH | /api/secretaire/rdv/{id}/annuler | Annuler un RDV | SECRETAIRE |

---

## Médecin

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | /api/medecin/plannings | Mes plannings | MEDECIN |
| GET | /api/medecin/rdv | Tous mes RDV | MEDECIN |
| GET | /api/medecin/rdv/en-attente | Mes RDV en attente | MEDECIN |
| PATCH | /api/medecin/rdv/{id}/confirmer | Confirmer un RDV | MEDECIN |
| PATCH | /api/medecin/rdv/{id}/terminer | Terminer un RDV | MEDECIN |
| GET | /api/medecin/stats | Mes statistiques | MEDECIN |

---

## Patient

| Méthode | Endpoint | Description | Accès |
|---------|----------|-------------|-------|
| GET | /api/patient/cabinets | Cabinets actifs | PATIENT |
| GET | /api/patient/cabinets/{id} | Détails d'un cabinet | PATIENT |
| GET | /api/patient/specialites | Toutes les spécialités | PATIENT |
| GET | /api/patient/specialites/cabinet/{id} | Spécialités d'un cabinet | PATIENT |
| GET | /api/patient/medecins | Médecins (filtres: specialite_id, cabinet_id) | PATIENT |
| GET | /api/patient/medecins/{id} | Détails d'un médecin | PATIENT |
| GET | /api/patient/medecins/{id}/disponibilites | Créneaux disponibles | PATIENT |
| POST | /api/patient/rdv | Demander un RDV | PATIENT |
| GET | /api/patient/rdv | Tous mes RDV | PATIENT |
| GET | /api/patient/rdv/en-attente | Mes RDV en attente | PATIENT |
| GET | /api/patient/rdv/confirmes | Mes RDV confirmés | PATIENT |
| GET | /api/patient/rdv/historique | Historique (terminés + annulés) | PATIENT |
| GET | /api/patient/rdv/{id} | Détails d'un RDV | PATIENT |
| PUT | /api/patient/rdv/{id}/annuler | Annuler mon RDV | PATIENT |

---

## Récap par rôle

| Rôle | Nombre d'endpoints |
|------|-------------------|
| Public / Authentifié | 7 |
| SUPER_ADMIN | 9 |
| ADMIN | 18 |
| SECRETAIRE | 9 |
| MEDECIN | 6 |
| PATIENT | 14 |
| **Total** | **63** |
