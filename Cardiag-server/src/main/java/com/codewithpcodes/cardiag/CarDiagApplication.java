package com.codewithpcodes.cardiag;

import com.codewithpcodes.cardiag.auth.AuthenticationService;
import com.codewithpcodes.cardiag.auth.CreateAdminRequest;
import com.codewithpcodes.cardiag.embedding.EmbeddingPipelineService;
import com.codewithpcodes.cardiag.fault.FaultIngestionService;
import com.codewithpcodes.cardiag.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarDiagApplication {

    @Value("${application.ingestion.enabled}")
    private boolean ingestionEnabled;

    @Value("${application.ingestion.json-path}")
    private String jsonPath;

    @Value("${application.embedding.enabled}")
    private boolean embeddingEnabled;

    public static void main(String[] args) {
        SpringApplication.run(CarDiagApplication.class, args);
    }

    @Bean
    @Order(1)
    CommandLineRunner initAdmin(AuthenticationService service, UserRepository userRepository) {
        return args -> {
            String defaultEmail = "admin@ebenezer.com";
            if (!userRepository.existsByEmail(defaultEmail)) {
                var admin = new CreateAdminRequest(
                        "admin",
                        "pcodes",
                        defaultEmail,
                        "password123"
                );
                System.out.println("Admin token: " + service.createAdmin(admin).accessToken());
            } else {
                System.out.println("Default Admin already exists. Skipping creation...");
            }
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner runIngestion(FaultIngestionService ingestionService) {
        return args -> {
            if (!ingestionEnabled) {
                System.out.println("Fault ingestion disabled. Set app.ingestion.enabled=true to run.");
                return;
            }
            System.out.println("Starting fault ingestion from: " + jsonPath);
            ingestionService.ingestFromJsonFile(jsonPath);
            System.out.println("Fault ingestion complete.");
        };
    }

    @Bean
    @Order(3)
    CommandLineRunner runEmbedding(EmbeddingPipelineService embeddingPipelineService) {
        return args -> {
            if (!embeddingEnabled) {
                System.out.println("Embedding pipeline disabled. Set app.embedding.enabled=true to run.");
                return;
            }
            System.out.println("Starting embedding pipeline...");
            embeddingPipelineService.runPipeline();
            System.out.println("Embedding pipeline complete.");
        };
    }
}
