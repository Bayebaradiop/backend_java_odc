// package com.medibook.common.config.seeder;

// import com.medibook.cabinet.entity.Cabinet;
// import com.medibook.cabinet.repository.CabinetRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class CabinetSeeder implements Seeder {

//     private final CabinetRepository cabinetRepository;

//     @Override
//     public String getName() {
//         return "CabinetSeeder";
//     }

//     @Override
//     public int getOrder() {
//         return 1;  // Premier à s'exécuter
//     }

//     @Override
//     public boolean shouldRun() {
//         return cabinetRepository.count() == 0;
//     }

//     @Override
//     public void run() {
//         log.info("🌱 Exécution de CabinetSeeder...");

//         // Créer le cabinet principal
//         Cabinet cabinet = Cabinet.builder()
//                 .nom("Cabinet Médical Medibook")
//                 .adresse("123 Avenue de la Santé, Dakar Plateau")
//                 .telephone("+221 33 123 45 67")
//                 .email("contact@medibook.com")
//                 .couleurPrimaire("#007bff")
//                 .couleurSecondaire("#ffffff")
//                 .status(Cabinet.Status.ACTIF)
//                 .build();
//         cabinetRepository.save(cabinet);
//         log.info("   ✅ Cabinet créé: {}", cabinet.getNom());

//         // Créer un deuxième cabinet
//         Cabinet cabinet2 = Cabinet.builder()
//                 .nom("Cabinet Médical Sud")
//                 .adresse("45 Rue des Médecins, Dakar Fann")
//                 .telephone("+221 33 987 65 43")
//                 .email("sud@medibook.com")
//                 .couleurPrimaire("#28a745")
//                 .couleurSecondaire("#f8f9fa")
//                 .status(Cabinet.Status.ACTIF)
//                 .build();
//         cabinetRepository.save(cabinet2);
//         log.info("   ✅ Cabinet créé: {}", cabinet2.getNom());

//         log.info("✅ CabinetSeeder terminé - {} cabinets créés", cabinetRepository.count());
//     }
// }
