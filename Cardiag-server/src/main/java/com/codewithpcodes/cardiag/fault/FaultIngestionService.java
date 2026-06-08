package com.codewithpcodes.cardiag.fault;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaultIngestionService {

    private final FaultRepository faultRepository;
    private final ObjectMapper objectMapper;

    private static final int BATCH_SIZE = 100;


    @Transactional
    public void ingestFromJsonFile(String filePath) {
        log.info("Starting fault ingestion from: {}", filePath);

        FaultDatabaseDTO database = readJsonFile(filePath);
        log.info("JSON loaded. Metadata: title='{}', version='{}', totalCodes={}",
                database.getMetadata().getTitle(),
                database.getMetadata().getVersion(),
                database.getMetadata().getTotalCodes());

        List<FaultCodeDTO> codes = database.getCodes();
        log.info("Found {} fault code records in file", codes.size());

        int saved = 0;
        int skipped = 0;
        int failed = 0;

        List<Fault> batch = new ArrayList<>();

        for (FaultCodeDTO dto : codes) {

            // Skip if already in database — safe to re-run ingestion
            if (faultRepository.existsById(dto.getId())) {
                skipped++;
                continue;
            }

            try {
                Fault fault = mapToFault(dto);
                batch.add(fault);

                // Save in batches for performance
                if (batch.size() >= BATCH_SIZE) {
                    faultRepository.saveAll(batch);
                    saved += batch.size();
                    log.info("Batch saved. Total saved so far: {}", saved);
                    batch.clear();
                }

            } catch (Exception e) {
                log.warn("Skipping record '{}' due to mapping error: {}", dto.getId(), e.getMessage());
                failed++;
            }
        }

        // Save any remaining records that didn't fill a full batch
        if (!batch.isEmpty()) {
            faultRepository.saveAll(batch);
            saved += batch.size();
        }

        log.info("Ingestion complete. Saved: {}, Skipped (already exist): {}, Failed: {}",
                saved, skipped, failed);

    }

    private FaultDatabaseDTO readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("JSON file not found at path: " + filePath);
            }
            return objectMapper.readValue(file, FaultDatabaseDTO.class);
        } catch (IOException e) {
            log.warn("Error reading file: {}", filePath, e);
            throw new RuntimeException("Failed to read or parse JSON file: " + e.getMessage(), e);
        }
    }

    private Fault mapToFault(FaultCodeDTO dto) {
        return Fault.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(parseCategory(dto.getCategory()))
                .urgency(parseUrgency(dto.getUrgency()))
                .warningLight(parseWarningLight(dto.getWarningLight()))
                .symptoms(dto.getSymptoms() != null ? dto.getSymptoms() : new ArrayList<>())
                .causes(dto.getCauses() != null ? dto.getCauses() : new ArrayList<>())
                .repairTips(dto.getRepairTips() != null ? dto.getRepairTips() : new ArrayList<>())
                .build();
    }

    private FaultCategory parseCategory(String value) {
        if (value == null || value.isBlank()) {
            return FaultCategory.UNKNOWN;
        }
        try {
            return FaultCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unrecognized fault category '{}', defaulting to UNKNOWN", value);
            return FaultCategory.UNKNOWN;
        }
    }

    private UrgencyLevel parseUrgency(String value) {
        if (value == null || value.isBlank()) {
            return UrgencyLevel.LOW;
        }

        try {
            return UrgencyLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unrecognized urgency '{}', defaulting to LOW", value);
            return UrgencyLevel.LOW;
        }
    }

    private WarningLight parseWarningLight(String value) {
        if (value == null || value.isBlank()) return WarningLight.UNKNOWN;
        try {
            return WarningLight.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unrecognized warning light '{}', defaulting to UNKNOWN", value);
            return WarningLight.UNKNOWN;
        }
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : "";
    }
}
