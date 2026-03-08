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
                             nom VARCHAR(255) NOT NULL,
                             description TEXT,
                             cabinet_id BIGINT NOT NULL,

                             CONSTRAINT fk_specialite_cabinet
                                 FOREIGN KEY (cabinet_id)
                                     REFERENCES cabinets(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT unique_specialite_cabinet
                                 UNIQUE (nom, cabinet_id)
);

CREATE TABLE utilisateurs (
                              id BIGSERIAL PRIMARY KEY,
                              prenom VARCHAR(255),
                              nom VARCHAR(255),
                              email VARCHAR(255) UNIQUE,
                              telephone VARCHAR(20) UNIQUE,
                              mot_de_passe VARCHAR(255),
                              photo VARCHAR(255),
                              role VARCHAR(20),
                              status VARCHAR(10) DEFAULT 'ACTIF',

                              cabinet_id BIGINT,
                              specialite_id BIGINT,

                              CONSTRAINT fk_utilisateur_cabinet
                                  FOREIGN KEY (cabinet_id)
                                      REFERENCES cabinets(id)
                                      ON DELETE SET NULL,

                              CONSTRAINT fk_utilisateur_specialite
                                  FOREIGN KEY (specialite_id)
                                      REFERENCES specialites(id)
                                      ON DELETE SET NULL
);

CREATE TABLE template_semaine (
                                  id BIGSERIAL PRIMARY KEY,
                                  medecin_id BIGINT NOT NULL,
                                  jour_semaine VARCHAR(10) NOT NULL,
                                  heure_debut TIME NOT NULL,
                                  heure_fin TIME NOT NULL,
                                  duree_creneau INT NOT NULL,

                                  CONSTRAINT fk_template_medecin
                                      FOREIGN KEY (medecin_id)
                                          REFERENCES utilisateurs(id)
                                          ON DELETE CASCADE
);

CREATE TABLE exceptions_planning (
                                     id BIGSERIAL PRIMARY KEY,
                                     medecin_id BIGINT NOT NULL,
                                     date DATE NOT NULL,
                                     type VARCHAR(20) NOT NULL,

                                     CONSTRAINT fk_exception_medecin
                                         FOREIGN KEY (medecin_id)
                                             REFERENCES utilisateurs(id)
                                             ON DELETE CASCADE
);



CREATE TABLE rendez_vous (
                             id BIGSERIAL PRIMARY KEY,
                             patient_id BIGINT NOT NULL,
                             medecin_id BIGINT NOT NULL,
                             cabinet_id BIGINT NOT NULL,
                             date DATE NOT NULL,
                             heure TIME NOT NULL,
                             statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
                             motif TEXT,

                             CONSTRAINT fk_rdv_patient
                                 FOREIGN KEY (patient_id)
                                     REFERENCES utilisateurs(id),

                             CONSTRAINT fk_rdv_medecin
                                 FOREIGN KEY (medecin_id)
                                     REFERENCES utilisateurs(id),

                             CONSTRAINT fk_rdv_cabinet
                                 FOREIGN KEY (cabinet_id)
                                     REFERENCES cabinets(id)
);

CREATE TABLE creneaux (
                          id BIGSERIAL PRIMARY KEY,
                          medecin_id BIGINT NOT NULL,
                          date DATE NOT NULL,
                          heure_debut TIME NOT NULL,
                          heure_fin TIME NOT NULL,
                          disponible BOOLEAN DEFAULT TRUE,
                          rendez_vous_id BIGINT,

                          CONSTRAINT fk_creneau_medecin
                              FOREIGN KEY (medecin_id)
                                  REFERENCES utilisateurs(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_creneau_rdv
                              FOREIGN KEY (rendez_vous_id)
                                  REFERENCES rendez_vous(id)
                                  ON DELETE SET NULL
);