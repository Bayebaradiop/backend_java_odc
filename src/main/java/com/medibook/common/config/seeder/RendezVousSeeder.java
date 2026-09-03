package com.medibook.common.config.seeder;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.StatutRdv;
import com.medibook.creneau.entity.Creneau;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RendezVousSeeder implements Seeder {

    private final RendezVousRepository rendezVousRepository;
    private final UserRepository userRepository;
    private final CreneauRepository creneauRepository;

    private static final String[] MOTIFS = {
        "Consultation de contrôle - Tension artérielle",
        "Première consultation - Douleurs abdominales",
        "Suivi traitement - Diabète type 2",
        "Consultation générale - Fièvre et fatigue",
        "Bilan de santé annuel",
        "Douleurs thoraciques - Avis cardiologique",
        "Consultation pédiatrique - Vaccination",
        "Contrôle de la vue - Renouvellement lunettes",
        "Consultation dermatologique - Éruption cutanée",
        "Suivi grossesse - 3ème trimestre",
        "Mal de dos chronique - Consultation orthopédique"
    };

    private static final StatutRdv[] STATUTS_MIX = {
        StatutRdv.TERMINE, StatutRdv.TERMINE,
        StatutRdv.CONFIRME, StatutRdv.CONFIRME, StatutRdv.CONFIRME,
        StatutRdv.ANNULE
    };

    @Override
    public String getName() {
        return "RendezVousSeeder";
    }

    @Override
    public int getOrder() {
        return 7;
    }

    @Override
    public boolean shouldRun() {
        return rendezVousRepository.count() == 0;
    }

    @Override
    @Transactional
    public void run() {
        log.info("🌱 Exécution de RendezVousSeeder...");

        List<Utilisateur> patients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.PATIENT)
                .toList();

        if (patients.isEmpty()) {
            log.warn("   ⚠️ Aucun patient trouvé, rendez-vous ignorés");
            return;
        }

        Map<Long, List<Utilisateur>> medecinsByCabinet = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN && u.getCabinet() != null)
                .collect(Collectors.groupingBy(u -> u.getCabinet().getId()));

        if (medecinsByCabinet.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin avec cabinet trouvé");
            return;
        }

        int totalCreated = 0;
        int motifIndex = 0;

        for (Map.Entry<Long, List<Utilisateur>> entry : medecinsByCabinet.entrySet()) {
            Long cabinetId = entry.getKey();
            List<Utilisateur> medecins = entry.getValue();
            Cabinet cabinet = medecins.get(0).getCabinet();

            for (Utilisateur medecin : medecins) {
                List<Creneau> creneauxDisponibles = new ArrayList<>(
                    creneauRepository.findByMedecinId(medecin.getId()).stream()
                        .filter(Creneau::getDisponible)
                        .toList()
                );

                int nbRdv = Math.min(6, creneauxDisponibles.size());

                for (int i = 0; i < nbRdv && i < STATUTS_MIX.length; i++) {
                    Utilisateur patient = patients.get(i % patients.size());
                    StatutRdv statut = STATUTS_MIX[i];
                    String motif = MOTIFS[motifIndex % MOTIFS.length];
                    motifIndex++;

                    RendezVous.RendezVousBuilder builder = RendezVous.builder()
                            .patient(patient)
                            .medecin(medecin)
                            .cabinet(cabinet)
                            .statut(statut)
                            .motif(motif);

                    if (!creneauxDisponibles.isEmpty()) {
                        Creneau creneau = creneauxDisponibles.remove(0);
                        creneau.setDisponible(false);
                        creneauRepository.save(creneau);
                        builder.creneau(creneau);
                    }

                    rendezVousRepository.save(builder.build());
                    totalCreated++;
                }

                log.info("      ✅ {} RDV créés pour Dr. {} {}", nbRdv, medecin.getPrenom(), medecin.getNom());
            }
        }

        log.info("✅ RendezVousSeeder terminé - {} rendez-vous créés", totalCreated);
    }
}
