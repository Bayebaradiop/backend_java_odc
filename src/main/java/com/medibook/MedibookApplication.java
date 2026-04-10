package com.medibook;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableMethodSecurity
@EnableAsync
@EnableScheduling
public class MedibookApplication {

    public static void main(String[] args) {
        // Only load dotenv if .env file exists (local development)
        File envFile = new File(".env");
        if (envFile.exists()) {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        }

        SpringApplication.run(MedibookApplication.class, args);
    }
}
