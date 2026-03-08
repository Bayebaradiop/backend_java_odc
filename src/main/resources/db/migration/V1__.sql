CREATE TABLE cabinets (
                          id BIGSERIAL PRIMARY KEY,
                          nom VARCHAR(255) NOT NULL,
                          logo VARCHAR(255),
                          couleur_primaire VARCHAR(20),
                          couleur_secondaire VARCHAR(20),
                          adresse VARCHAR(255),
                          telephone VARCHAR(20),
                          email VARCHAR(255),
                          status VARCHAR(10) DEFAULT 'ACTIF'
);

CREATE TABLE specialites (
                             id BIGSERIAL PRIMARY KEY,
                             nom VARCHAR(255) NOT NULL UNIQUE,
                             description TEXT
);

CREATE TABLE utilisateur (
                             id BIGSERIAL PRIMARY KEY,
                             prenom VARCHAR(255),
                             nom VARCHAR(255),
                             email VARCHAR(255) UNIQUE,
                             telephone VARCHAR(20) UNIQUE,
                             mot_de_passe VARCHAR(255),
                             photo VARCHAR(255),
                             role VARCHAR(20),
                             status VARCHAR(10) DEFAULT 'ACTIF',
                             cabinet_id BIGINT REFERENCES cabinets(id),
                             specialite_id BIGINT REFERENCES specialites(id)
);

CREATE TABLE template_semaine (
                                  id BIGSERIAL PRIMARY KEY,
                                  medecin_id BIGINT REFERENCES utilisateur(id),
                                  jour_semaine VARCHAR(10),
                                  heure_debut TIME,
                                  heure_fin TIME,
                                  duree_creneau INT
);

CREATE TABLE exceptions_planning (
                                     id BIGSERIAL PRIMARY KEY,
                                     medecin_id BIGINT REFERENCES utilisateur(id),
                                     date DATE,
                                     type VARCHAR(20)
);

CREATE TABLE rendez_vous (
                             id BIGSERIAL PRIMARY KEY,
                             patient_id BIGINT REFERENCES utilisateur(id),
                             medecin_id BIGINT REFERENCES utilisateur(id),
                             cabinet_id BIGINT REFERENCES cabinets(id),
                             date DATE,
                             heure TIME,
                             statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
                             motif TEXT
);