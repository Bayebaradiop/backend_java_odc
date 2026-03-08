##  SCHÉMAS DE BASE DE DONNÉES - MEDIBOOK SaaS

┌────────────────────────────────────────────────────────────┐
│                        CABINETS                            │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  nomCabinet      VARCHAR(255)       (NOT NULL)             │
│  adresse         VARCHAR(255)       (NOT NULL)             │
│  telephone       VARCHAR(20)        (NOT NULL)             │
│  email           VARCHAR(255)       (UNIQUE, NULL)         │
│  logo            VARCHAR(255)       (NULL)                 │
│  status          ENUM               (DEFAULT 'actif')      │
│                  (actif | inactif)                         │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (1:N) → users (un cabinet a plusieurs employés)           │
│  (1:N) → specialites (un cabinet a ses propres spécialités)│
└────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────┐
│                       SPECIALITES                          │
│              (Propres à chaque cabinet)                    │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  nomSpecialite   VARCHAR(255)       (NOT NULL)             │
│  description     TEXT               (NULL)                 │
│  cabinet_id      BIGINT UNSIGNED    (FK → cabinets.id)     │
│                  (NOT NULL)                                │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  CONTRAINTES:                                              │
│  (UNIQUE) → (cabinet_id, nomSpecialite)                    │
│             (pas de doublon dans un même cabinet)          │
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (N:1) → cabinets (une spécialité appartient à un cabinet) │
│  (1:N) → users (une spécialité a plusieurs médecins)       │
└────────────────────────────────────────────────────────────┘


┌────────────────────────────────────────────────────────────┐
│                          USERS                             │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  prenom          VARCHAR(255)       (NOT NULL)             │
│  nom             VARCHAR(255)       (NOT NULL)             │
│  email           VARCHAR(255)       (NOT NULL, UNIQUE)     │
│  telephone       VARCHAR(20)        (NOT NULL, UNIQUE)     │
│  adresse         VARCHAR(255)       (NOT NULL)             │
│  sexe            ENUM('M','F')      (NOT NULL)             │
│  motDePasse      VARCHAR(255)       (NOT NULL)             │
│  role            ENUM               (NOT NULL)             │
│                  (super_admin | admin | secretaire |       │
│                   medecin | patient)                       │
│  photo           VARCHAR(255)       (NULL)                 │
│  status          TINYINT            (DEFAULT 0)            │
│                  (0=actif, 1=bloqué)                       │
│  cabinet_id      BIGINT UNSIGNED    (FK → cabinets.id)     │
│                  (NULL si patient ou super_admin)          │
│  specialite_id   BIGINT UNSIGNED    (FK → specialites.id)  │
│                  (NULL si role ≠ medecin)                  │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (N:1) → cabinets (un user appartient à un cabinet)        │
│  (N:1) → specialites (un médecin a une spécialité)         │
│  (1:N) → plannings (un médecin a plusieurs plannings)      │
│  (1:N) → rendez_vous (un patient a plusieurs RDV)          │
│  (1:N) → rendez_vous (un médecin a plusieurs RDV)          │
└────────────────────────────────────────────────────────────┘

(super_admin) → cabinet_id = NULL, specialite_id = NULL
(admin)       → cabinet_id = ID,   specialite_id = NULL
(secretaire)  → cabinet_id = ID,   specialite_id = NULL
(medecin)     → cabinet_id = ID,   specialite_id = ID
(patient)     → cabinet_id = NULL, specialite_id = NULL

┌────────────────────────────────────────────────────────────┐
│                        PLANNINGS                           │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  medecin_id      BIGINT UNSIGNED    (FK → users.id)        │
│                  (NOT NULL, role='medecin')                │
│  datePlanning    DATE               (NOT NULL)             │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  CONTRAINTES:                                              │
│  (UNIQUE) → (medecin_id, datePlanning)                     │
│             (un médecin = un planning par jour)            │
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (N:1) → users (un planning appartient à un médecin)       │
│  (1:N) → creneaux (un planning a plusieurs créneaux)       │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                        CRENEAUX                            │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  planning_id     BIGINT UNSIGNED    (FK → plannings.id)    │
│                  (NOT NULL)                                │
│  heureDebut      TIME               (NOT NULL)             │
│  heureFin        TIME               (NOT NULL)             │
│  status          ENUM               (DEFAULT 'disponible') │
│                  (disponible | reserve)                    │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (N:1) → plannings (un créneau appartient à un planning)   │
│  (1:1) → rendez_vous (un créneau = un seul RDV)           │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                       RENDEZ_VOUS                          │
├────────────────────────────────────────────────────────────┤
│  id              BIGINT UNSIGNED    (PK, AUTO_INCREMENT)   │
│  patient_id      BIGINT UNSIGNED    (FK → users.id)        │
│                  (NOT NULL, role='patient')                │
│  medecin_id      BIGINT UNSIGNED    (FK → users.id)        │
│                  (NOT NULL, role='medecin')                │
│  creneau_id      BIGINT UNSIGNED    (FK → creneaux.id)     │
│                  (NOT NULL, UNIQUE)                        │
│  motif           TEXT               (NULL)                 │
│  status          ENUM               (DEFAULT 'en_attente') │
│                  (en_attente | confirme | annule | refuse) │
│  created_at      TIMESTAMP          (NULL)                 │
│  updated_at      TIMESTAMP          (NULL)                 │
├────────────────────────────────────────────────────────────┤
│  CONTRAINTES:                                              │
│  (UNIQUE) → creneau_id (un créneau = un seul RDV actif)    │
├────────────────────────────────────────────────────────────┤
│   VALIDATIONS MÉTIER (à vérifier dans le code Java):     │
│  - Un patient ne peut pas avoir 2 RDV au même horaire      │
│  - Le créneau doit être 'disponible' avant réservation     │
│  - Passer le créneau en 'reserve' après création du RDV    │
│  - Repasser le créneau en 'disponible' si RDV annulé/refusé│
├────────────────────────────────────────────────────────────┤
│  RELATIONS:                                                │
│  (N:1) → users/patient (un RDV a un patient)               │
│  (N:1) → users/medecin (un RDV a un médecin)               │
│  (1:1) → creneaux (un RDV est sur un créneau unique)       │
└────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     TOUTES LES RELATIONS                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  CABINETS (1) ────────< (N) USERS                               │
│      "Un cabinet a plusieurs employés"                          │
│                                                                 │
│  CABINETS (1) ────────< (N) SPECIALITES                         │
│      "Un cabinet a ses propres spécialités"                     │
│      "Chaque admin crée les spécialités de son cabinet"         │
│                                                                 │
│  SPECIALITES (1) ────────< (N) USERS                            │
│      "Une spécialité a plusieurs médecins"                      │
│                                                                 │
│  USERS/medecin (1) ────────< (N) PLANNINGS                      │
│      "Un médecin a plusieurs plannings"                         │
│                                                                 │
│  PLANNINGS (1) ────────< (N) CRENEAUX                           │
│      "Un planning a plusieurs créneaux"                         │
│                                                                 │
│  CRENEAUX (1) ────────── (1) RENDEZ_VOUS                        │
│      "Un créneau = un seul RDV (relation 1:1)"                  │
│                                                                 │
│  USERS/patient (1) ────────< (N) RENDEZ_VOUS                    │
│      "Un patient a plusieurs RDV"                               │
│                                                                 │
│  USERS/medecin (1) ────────< (N) RENDEZ_VOUS                    │
│      "Un médecin a plusieurs RDV"                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                    DIAGRAMME VISUEL                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│         ┌─────────────┐                                        │
│         │  CABINETS   │                                        │
│         └──────┬──────┘                                        │
│                │                                               │
│           ┌────┴────┐                                          │
│           │         │                                          │
│           │ 1:N     │ 1:N                                      │
│           ↓         ↓                                          │
│    ┌─────────────┐  ┌──────────────┐                           │
│    │    USERS    │  │ SPECIALITES  │                           │
│    │  (5 rôles)  │←─│ (du cabinet) │                           │
│    └──────┬──────┘  └──────────────┘                           │
│           │         (medecin.specialite_id → specialites.id)   │
│              └──────┬──────┘                                    │
│                     │                                           │
│                     │ 1:N (medecin)                             │
│                     ↓                                           │
│              ┌─────────────┐                                    │
│              │  PLANNINGS  │                                    │
│              └──────┬──────┘                                    │
│                     │                                           │
│                     │ 1:N                                       │
│                     ↓                                           │
│              ┌─────────────┐                                    │
│              │  CRENEAUX   │                                    │
│              └──────┬──────┘                                    │
│                     │                                           │
│                     │ 1:N                                       │
│                     ↓                                           │
│              ┌─────────────┐                                    │
│              │ RENDEZ_VOUS │                                    │
│              └─────────────┘                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                     RÉSUMÉ DES TABLES                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Table              │ Description                               │
│  ───────────────────┼─────────────────────────────────────────  │
│  cabinets           │ Cabinets médicaux                         │
│  specialites        │ Spécialités (propres à chaque cabinet)    │
│  users              │ Utilisateurs (5 rôles)                    │
│  plannings          │ Planning d'un médecin                     │
│  creneaux           │ Créneaux horaires                         │
│  rendez_vous        │ Rendez-vous patient/médecin               │
│                                                                 │
│  TOTAL: 6 tables                                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘


