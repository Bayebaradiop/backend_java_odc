-- V2__update_exceptions_planning.sql

-- Ajouter gestion période
ALTER TABLE exceptions_planning
ADD COLUMN date_debut DATE,
ADD COLUMN date_fin DATE;

-- Ajouter gestion plage horaire
ALTER TABLE exceptions_planning
ADD COLUMN heure_debut TIME,
ADD COLUMN heure_fin TIME;

-- Copier la valeur existante "date" vers date_debut
UPDATE exceptions_planning
SET date_debut = date;

-- Si date_fin est NULL on met la même date
UPDATE exceptions_planning
SET date_fin = date_debut
WHERE date_fin IS NULL;

-- Supprimer l'ancienne colonne
ALTER TABLE exceptions_planning
DROP COLUMN date;

-- Contraintes de cohérence
ALTER TABLE exceptions_planning
ADD CONSTRAINT chk_exception_dates
CHECK (date_fin >= date_debut);

ALTER TABLE exceptions_planning
ADD CONSTRAINT chk_exception_heures
CHECK (
    (heure_debut IS NULL AND heure_fin IS NULL)
    OR
    (heure_debut IS NOT NULL AND heure_fin IS NOT NULL AND heure_fin > heure_debut)
);

-- Index important pour la recherche
CREATE INDEX idx_exception_medecin_dates
ON exceptions_planning(medecin_id, date_debut, date_fin);