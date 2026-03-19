-- Ajout des colonnes heureDebut, heureFin et motif à la table exceptions_planning
ALTER TABLE exceptions_planning ADD COLUMN heure_debut TIME;
ALTER TABLE exceptions_planning ADD COLUMN heure_fin TIME;
ALTER TABLE exceptions_planning ADD COLUMN motif VARCHAR(500);
