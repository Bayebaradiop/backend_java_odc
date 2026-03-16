package com.medibook.common.config.seeder;

import com.medibook.ExceptionsPlanning.entity.ExceptionPlanning;
import com.medibook.ExceptionsPlanning.repository.ExceptionsPlanningRepository;
import com.medibook.common.enums.TypeException;
import com.medibook.common.enums.Role;
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
public class ExceptionsPlanningSeeder implements Seeder {

    private final ExceptionsPlanningRepository exceptionsPlanningRepository;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return "ExceptionsPlanningSeeder";
    }

    @Override
    public int getOrder() {
        return 7;  // Dernier seeder
    }

    @Override
    public boolean shouldRun() {
        return exceptionsPlanningRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de ExceptionsPlanningSeeder...");

        // Récupérer les médecins
        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        if (medecins.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin trouvé, exceptions ignorées");
            return;
        }

        // Jours fériés du Sénégal (dates fixes)
        LocalDate[] joursFeries = {
            LocalDate.of(LocalDate.now().getYear(), 4, 4),   // Indépendance
            LocalDate.of(LocalDate.now().getYear(), 5, 1),   // Fête du travail
            LocalDate.of(LocalDate.now().getYear(), 8, 15),  // Assomption
            LocalDate.of(LocalDate.now().getYear(), 11, 1),  // Toussaint
            LocalDate.of(LocalDate.now().getYear(), 11, 2), // Jour des morts
            LocalDate.of(LocalDate.now().getYear(), 12, 25), // Noël
        };

        // Créer des jours fériés pour le premier médecin
        Utilisateur medecin = medecins.get(0);
        
        for (LocalDate jourFerie : joursFeries) {
            // Only add if the date is in the future or recent
            if (!jourFerie.isBefore(LocalDate.now().minusDays(30))) {
                ExceptionPlanning exception = ExceptionPlanning.builder()
                        .medecin(medecin)
                        .dateDebut(jourFerie)
                        .dateFin(jourFerie)
                        .type(TypeException.FERME)
                        .build();
                exceptionsPlanningRepository.save(exception);
                log.info("   ✅ Jour férié: {} ({})", jourFerie, TypeException.FERME);
            }
        }

        // Vacances pour le médecin (dates futures)
        LocalDate debutVacances = LocalDate.now().plusMonths(2);
        LocalDate finVacances = debutVacances.plusDays(5);
        
        for (LocalDate date = debutVacances; !date.isAfter(finVacances); date = date.plusDays(1)) {
            ExceptionPlanning vacances = ExceptionPlanning.builder()
                    .medecin(medecin)
                    .dateDebut(date)
                    .dateFin(date)
                    .type(TypeException.VACANCES)
                    .build();
            exceptionsPlanningRepository.save(vacances);
        }
        log.info("   ✅ Vacances: {} au {} ({})", debutVacances, finVacances, TypeException.VACANCES);

        // Créer quelques absences supplémentaires
        LocalDate absence1 = LocalDate.now().plusWeeks(1);
        ExceptionPlanning absence = ExceptionPlanning.builder()
                .medecin(medecin)
                .dateDebut(absence1)
                .dateFin(absence1)
                .type(TypeException.ABSENT)
                .build();
        exceptionsPlanningRepository.save(absence);
        log.info("   ✅ Absence: {} ({})", absence1, TypeException.ABSENT);

        // Si un deuxième médecin existe, ajouter quelques exceptions
        if (medecins.size() > 1) {
            Utilisateur medecin2 = medecins.get(1);
            
            // Vacances pour le médecin 2
            LocalDate debutVac2 = LocalDate.now().plusMonths(3);
            for (LocalDate date = debutVac2; date.isBefore(debutVac2.plusDays(7)); date = date.plusDays(1)) {
                ExceptionPlanning vacances2 = ExceptionPlanning.builder()
                        .medecin(medecin2)
                        .dateDebut(date)
                        .dateFin(date)
                        .type(TypeException.VACANCES)
                        .build();
                exceptionsPlanningRepository.save(vacances2);
            }
            log.info("   ✅ Vacances pour Dr. {} (médecin 2)", medecin2.getNom());
        }

        log.info("✅ ExceptionsPlanningSeeder terminé - {} exceptions créées", exceptionsPlanningRepository.count());
    }
}
