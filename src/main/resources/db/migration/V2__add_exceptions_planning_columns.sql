-- Ajout des colonnes heureDebut, heureFin et motif à la table exceptions_planning
ALTER TABLE exceptions_planning ADD COLUMN IF NOT EXISTS heure_debut TIME;
ALTER TABLE exceptions_planning ADD COLUMN IF NOT EXISTS heure_fin TIME;
ALTER TABLE exceptions_planning ADD COLUMN IF NOT EXISTS motif VARCHAR(500);
