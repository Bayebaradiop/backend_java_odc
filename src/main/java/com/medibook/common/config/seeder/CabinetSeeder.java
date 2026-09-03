package com.medibook.common.config.seeder;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.repository.CabinetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CabinetSeeder implements Seeder {

    private final CabinetRepository cabinetRepository;

    @Override
    public String getName() {
        return "CabinetSeeder";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean shouldRun() {
        return cabinetRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de CabinetSeeder...");

        // Cabinet principal avec logo haute définition
        Cabinet cabinet1 = Cabinet.builder()
                .nom("Cabinet Médical Medibook")
                .logo("https://images.unsplash.com/photo-1629909613654-28e377c37b09?w=400&auto=format&fit=crop&q=80")
                .adresse("123 Avenue de la Santé, Dakar Plateau")
                .telephone("+221 33 123 45 67")
                .email("contact@medibook.com")
                .couleurPrimaire("#0F766E")
                .couleurSecondaire("#F0FDFA")
                .status(Cabinet.Status.ACTIF)
                .build();
        cabinetRepository.save(cabinet1);
        log.info("   ✅ Cabinet créé avec logo: {}", cabinet1.getNom());

        // Deuxième cabinet avec logo haute définition
        Cabinet cabinet2 = Cabinet.builder()
                .nom("Cabinet Médical Sud")
                .logo("https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=400&auto=format&fit=crop&q=80")
                .adresse("45 Rue des Médecins, Dakar Fann")
                .telephone("+221 33 987 65 43")
                .email("sud@medibook.com")
                .couleurPrimaire("#2563EB")
                .couleurSecondaire("#EFF6FF")
                .status(Cabinet.Status.ACTIF)
                .build();
        cabinetRepository.save(cabinet2);
        log.info("   ✅ Cabinet créé avec logo: {}", cabinet2.getNom());

        log.info("✅ CabinetSeeder terminé - {} cabinets créés", cabinetRepository.count());
    }
}
