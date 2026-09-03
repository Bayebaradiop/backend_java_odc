package com.medibook.common.config.seeder;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.specialite.entity.Specialite;
import com.medibook.specialite.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpecialiteSeeder implements Seeder {

    private final SpecialiteRepository specialiteRepository;
    private final CabinetRepository cabinetRepository;

    @Override
    public String getName() {
        return "SpecialiteSeeder";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean shouldRun() {
        return specialiteRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de SpecialiteSeeder...");

        List<Cabinet> cabinets = cabinetRepository.findAll();
        if (cabinets.isEmpty()) {
            log.warn("   ⚠️ Aucun cabinet trouvé, spécialités ignorées");
            return;
        }

        Cabinet cabinetPrincipal = cabinets.get(0);

        Specialite medecineGenerale = Specialite.builder()
                .nom("Médecine Générale")
                .description("Consultations de médecine générale pour tous les âges")
                .cabinet(cabinetPrincipal)
                .build();
        specialiteRepository.save(medecineGenerale);

        Specialite cardiologie = Specialite.builder()
                .nom("Cardiologie")
                .description("Diagnostic et traitement des maladies cardiovasculaires")
                .cabinet(cabinetPrincipal)
                .build();
        specialiteRepository.save(cardiologie);

        Specialite pediatrie = Specialite.builder()
                .nom("Pédiatrie")
                .description("Soins médicaux pour les enfants et adolescents")
                .cabinet(cabinetPrincipal)
                .build();
        specialiteRepository.save(pediatrie);

        Specialite dermatologie = Specialite.builder()
                .nom("Dermatologie")
                .description("Traitement des maladies de la peau")
                .cabinet(cabinetPrincipal)
                .build();
        specialiteRepository.save(dermatologie);

        Specialite gynecologie = Specialite.builder()
                .nom("Gynécologie")
                .description("Santé de la femme et obstétrique")
                .cabinet(cabinetPrincipal)
                .build();
        specialiteRepository.save(gynecologie);

        if (cabinets.size() > 1) {
            Cabinet cabinetSud = cabinets.get(1);

            Specialite ophtalmologie = Specialite.builder()
                    .nom("Ophtalmologie")
                    .description("Soins des yeux et de la vision")
                    .cabinet(cabinetSud)
                    .build();
            specialiteRepository.save(ophtalmologie);

            Specialite orthopedie = Specialite.builder()
                    .nom("Orthopédie")
                    .description("Traitement des troubles musculo-squelettiques")
                    .cabinet(cabinetSud)
                    .build();
            specialiteRepository.save(orthopedie);
        }

        log.info("✅ SpecialiteSeeder terminé - {} spécialités créées", specialiteRepository.count());
    }
}
