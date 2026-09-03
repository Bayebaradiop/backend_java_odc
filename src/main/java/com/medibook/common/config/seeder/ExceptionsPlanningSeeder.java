package com.medibook.common.config.seeder;

import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
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
        return 8;
    }

    @Override
    public boolean shouldRun() {
        return exceptionsPlanningRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de ExceptionsPlanningSeeder...");

        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        if (medecins.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin trouvé, exceptions ignorées");
            return;
        }

        LocalDate[] joursFeries = {
            LocalDate.of(LocalDate.now().getYear(), 4, 4),
            LocalDate.of(LocalDate.now().getYear(), 5, 1),
            LocalDate.of(LocalDate.now().getYear(), 8, 15),
            LocalDate.of(LocalDate.now().getYear(), 11, 1),
            LocalDate.of(LocalDate.now().getYear(), 12, 25)
        };

        Utilisateur medecin = medecins.get(0);
        
        for (LocalDate jourFerie : joursFeries) {
            if (!jourFerie.isBefore(LocalDate.now().minusDays(30))) {
                ExceptionsPlanning exception = ExceptionsPlanning.builder()
                        .medecin(medecin)
                        .dateDebut(jourFerie)
                        .dateFin(jourFerie)
                        .type(TypeException.FERME)
                        .build();
                exceptionsPlanningRepository.save(exception);
            }
        }

        LocalDate debutVacances = LocalDate.now().plusMonths(2);
        LocalDate finVacances = debutVacances.plusDays(5);
        
        for (LocalDate date = debutVacances; !date.isAfter(finVacances); date = date.plusDays(1)) {
            ExceptionsPlanning vacances = ExceptionsPlanning.builder()
                    .medecin(medecin)
                    .dateDebut(date)
                    .dateFin(date)
                    .type(TypeException.VACANCES)
                    .build();
            exceptionsPlanningRepository.save(vacances);
        }

        log.info("✅ ExceptionsPlanningSeeder terminé - {} exceptions créées", exceptionsPlanningRepository.count());
    }
}
