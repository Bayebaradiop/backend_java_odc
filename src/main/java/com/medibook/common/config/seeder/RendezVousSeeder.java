package com.medibook.common.config.seeder;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.StatutRdv;
import com.medibook.rendezvous.entity.RendezVous;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RendezVousSeeder implements Seeder {

    private final RendezVousRepository rendezVousRepository;
    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;

    @Override
    public String getName() {
        return "RendezVousSeeder";
    }

    @Override
    public int getOrder() {
        return 6;  // S'exécute après CreneauSeeder
    }

    @Override
    public boolean shouldRun() {
        return rendezVousRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de RendezVousSeeder...");

        // Récupérer les médecins et patients
        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        List<Utilisateur> patients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.PATIENT)
                .toList();

        List<Cabinet> cabinets = cabinetRepository.findAll();

        if (medecins.isEmpty() || patients.isEmpty() || cabinets.isEmpty()) {
            log.warn("   ⚠️ Données insuffisantes pour créer des rendez-vous");
            return;
        }

        Utilisateur medecin = medecins.get(0);
        Cabinet cabinet = cabinets.get(0);

        // Créer plusieurs rendez-vous avec différents statuts

        // 1. Rendez-vous passés - TERMINÉS
        LocalDate hier = LocalDate.now().minusDays(1);
        RendezVous rdv1 = RendezVous.builder()
                .patient(patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.TERMINE)
                .motif("Consultation de contrôle - Tension artérielle")
                .build();
        rendezVousRepository.save(rdv1);
        log.info("   ✅ RDV passé terminé: Patient {} avec Dr. {} {}", 
                patients.get(0).getNom(), medecin.getPrenom(), medecin.getNom());

        // 2. Rendez-vous de la semaine dernière - TERMINÉS
        LocalDate semaineDerniere = LocalDate.now().minusDays(5);
        RendezVous rdv2 = RendezVous.builder()
                .patient(patients.size() > 1 ? patients.get(1) : patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.TERMINE)
                .motif("Première consultation - Douleurs abdominales")
                .build();
        rendezVousRepository.save(rdv2);
        log.info("   ✅ RDV terminé: Patient avec Dr. {}", medecin.getNom());

        // 3. Rendez-vous aujourd'hui - CONFIRMÉ
        LocalDate aujourdhui = LocalDate.now();
        RendezVous rdv3 = RendezVous.builder()
                .patient(patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.CONFIRME)
                .motif("Suivi traitement - Diabète")
                .build();
        rendezVousRepository.save(rdv3);
        log.info("   ✅ RDV aujourd'hui confirmé: Patient {} avec Dr. {}", 
                patients.get(0).getNom(), medecin.getNom());

        // 4. Rendez-vous demain - EN ATTENTE
        LocalDate demain = LocalDate.now().plusDays(1);
        RendezVous rdv4 = RendezVous.builder()
                .patient(patients.size() > 1 ? patients.get(1) : patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.EN_ATTENTE)
                .motif("Consultation générale - Fièvre et fatigue")
                .build();
        rendezVousRepository.save(rdv4);
        log.info("   ✅ RDV demain en attente: Patient avec Dr. {}", medecin.getNom());

        // 5. Rendez-vous la semaine prochaine - EN ATTENTE
        LocalDate semaineProche = LocalDate.now().plusDays(3);
        RendezVous rdv5 = RendezVous.builder()
                .patient(patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.EN_ATTENTE)
                .motif("Bilan de santé annuel")
                .build();
        rendezVousRepository.save(rdv5);
        log.info("   ✅ RDV semaine prochaine: Patient {} avec Dr. {}", 
                patients.get(0).getNom(), medecin.getNom());

        // 6. Rendez-vous annulé
        RendezVous rdv6 = RendezVous.builder()
                .patient(patients.size() > 1 ? patients.get(1) : patients.get(0))
                .medecin(medecin)
                .cabinet(cabinet)
                .statut(StatutRdv.ANNULE)
                .motif("Consultation dermatologique (annulé par le patient)")
                .build();
        rendezVousRepository.save(rdv6);
        log.info("   ✅ RDV annulé: Patient avec Dr. {}", medecin.getNom());

        // Si un deuxième médecin existe, créer des rendez-vous pour lui aussi
        if (medecins.size() > 1 && patients.size() > 1) {
            Utilisateur medecin2 = medecins.get(1);
            
            RendezVous rdv7 = RendezVous.builder()
                    .patient(patients.get(1))
                    .medecin(medecin2)
                    .cabinet(cabinet)
                    .statut(StatutRdv.CONFIRME)
                    .motif("Consultation spécialisée - Cardiologie")
                    .build();
            rendezVousRepository.save(rdv7);
            log.info("   ✅ RDV pour Dr. {} (médecin 2)", medecin2.getNom());
        }

        log.info("✅ RendezVousSeeder terminé - {} rendez-vous créés", rendezVousRepository.count());
    }
}
