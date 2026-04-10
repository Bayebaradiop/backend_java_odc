-- V1__init.sql


CREATE TYPE role_enum AS ENUM (
    'SUPER_ADMIN',
    'ADMIN',
    'SECRETAIRE',
    'MEDECIN',
    'PATIENT'
    );

CREATE TYPE statut_enum AS ENUM (
    'ACTIF',
    'INACTIF',
    'SUSPENDED',
    'DELETED'
    );

CREATE TYPE typeException AS ENUM (
    'ABSENT',
    'FERME',
    'VACANCES'
    );

CREATE TYPE statusRdv AS ENUM (
    'EN_ATTENTE',
    'CONFIRME',
    'ANNULE',
    'TERMINE'
    );



-- 1️⃣ Cabinets
CREATE TABLE cabinets (
                          id BIGSERIAL PRIMARY KEY,
                          nom VARCHAR(255) NOT NULL,
                          logo VARCHAR(255),
                          couleur_primaire VARCHAR(20),
                          couleur_secondaire VARCHAR(20),
                          adresse VARCHAR(255),
                          telephone VARCHAR(20),
                          email VARCHAR(255),
                          status statut_enum NOT NULL DEFAULT 'ACTIF'
);

-- 2️⃣ Specialités (indépendantes par cabinet)
CREATE TABLE specialites (
                             id BIGSERIAL PRIMARY KEY,
                             nom VARCHAR(255) NOT NULL,
                             description TEXT,
                             cabinet_id BIGINT NOT NULL REFERENCES cabinets(id),
                             CONSTRAINT uq_nom_cabinet UNIQUE(nom, cabinet_id)
);

-- 3️⃣ Utilisateurs
CREATE TABLE utilisateurs (
                              id BIGSERIAL PRIMARY KEY,
                              prenom VARCHAR(255) NOT NULL,
                              nom VARCHAR(255) NOT NULL,
                              email VARCHAR(255) NOT NULL UNIQUE,
                              telephone VARCHAR(20) NOT NULL UNIQUE,
                              mot_de_passe VARCHAR(255) NOT NULL,
                              photo VARCHAR(255),
                              role role_enum NOT NULL,
                              status statut_enum NOT NULL DEFAULT 'ACTIF',
                              cabinet_id BIGINT REFERENCES cabinets(id),
                              specialite_id BIGINT REFERENCES specialites(id)
);

-- 4️⃣ Template semaine
CREATE TABLE template_semaine (
                                  id BIGSERIAL PRIMARY KEY,
                                  medecin_id BIGINT NOT NULL REFERENCES utilisateurs(id),
                                  jour_semaine VARCHAR(10) NOT NULL,
                                  heure_debut TIME NOT NULL,
                                  heure_fin TIME NOT NULL,
                                  duree_creneau INT NOT NULL
);

-- 5️⃣ Exceptions planning
CREATE TABLE exceptions_planning (
                                     id BIGSERIAL PRIMARY KEY,
                                     medecin_id BIGINT NOT NULL REFERENCES utilisateurs(id),
                                     date DATE NOT NULL,
                                     type typeException NOT NULL
);

-- 6️⃣ Rendez-vous (avant creneaux)
CREATE TABLE rendez_vous (
                             id BIGSERIAL PRIMARY KEY,
                             patient_id BIGINT NOT NULL REFERENCES utilisateurs(id),
                             medecin_id BIGINT NOT NULL REFERENCES utilisateurs(id),
                             cabinet_id BIGINT NOT NULL REFERENCES cabinets(id),
                             creneau_id BIGINT UNIQUE, -- on peut laisser NULL au début
                             statut statusRdv NOT NULL DEFAULT 'EN_ATTENTE',
                             motif TEXT
);

-- 7️⃣ Creneaux générés depuis TemplateSemaine
CREATE TABLE creneaux (
                          id BIGSERIAL PRIMARY KEY,
                          medecin_id BIGINT NOT NULL REFERENCES utilisateurs(id),
                          date DATE NOT NULL,
                          heure_debut TIME NOT NULL,
                          heure_fin TIME NOT NULL,
                          rendez_vous_id BIGINT UNIQUE REFERENCES rendez_vous(id) NULL ,
                          disponible BOOLEAN NOT NULL DEFAULT TRUE,
                          CONSTRAINT uq_creneau UNIQUE(medecin_id, date, heure_debut)
);