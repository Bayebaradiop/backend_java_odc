-- V3__update_exceptions_planning.sql
-- Ajouter colonnes seulement si elles n'existent pas
ALTER TABLE exceptions_planning
    ADD COLUMN IF NOT EXISTS date_debut DATE,
    ADD COLUMN IF NOT EXISTS date_fin DATE,
    ADD COLUMN IF NOT EXISTS heure_debut TIME,
    ADD COLUMN IF NOT EXISTS heure_fin TIME;

-- Copier la valeur existante "date" vers date_debut si la colonne existe encore
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='exceptions_planning' AND column_name='date'
    ) THEN
        UPDATE exceptions_planning SET date_debut = date;
        UPDATE exceptions_planning SET date_fin = date_debut WHERE date_fin IS NULL;
        ALTER TABLE exceptions_planning DROP COLUMN date;
    END IF;
END $$;

-- Contraintes (ignore si elles existent déjà)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_exception_dates'
    ) THEN
        ALTER TABLE exceptions_planning
        ADD CONSTRAINT chk_exception_dates
        CHECK (date_fin >= date_debut);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_exception_heures'
    ) THEN
        ALTER TABLE exceptions_planning
        ADD CONSTRAINT chk_exception_heures
        CHECK (
            (heure_debut IS NULL AND heure_fin IS NULL)
            OR
            (heure_debut IS NOT NULL AND heure_fin IS NOT NULL AND heure_fin > heure_debut)
        );
    END IF;
END $$;

-- Index (ignore si il existe déjà)
CREATE INDEX IF NOT EXISTS idx_exception_medecin_dates
ON exceptions_planning(medecin_id, date_debut, date_fin);