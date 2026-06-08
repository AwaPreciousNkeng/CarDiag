package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingPipelineService {

    private final FaultRepository faultRepository;
    private final FaultEmbeddingRepository faultEmbeddingRepository;
    private final EmbeddingService embeddingService;

    private static final int BATCH_SIZE = 50;

    public void runPipeline() {
        log.info("Starting embedding pipeline...");

        List<Fault> allFaults = faultRepository.findAll();
        log.info("Loaded {} faults from database.", allFaults.size());

        List<Fault> toEmbed = allFaults.stream()
                .filter(f -> !faultEmbeddingRepository.existsByFaultId(f.getId()))
                .toList();

        log.info("{} faults already have embeddings. {} need embedding.",
                allFaults.size() - toEmbed.size(), toEmbed.size());

        if (toEmbed.isEmpty()) {
            log.info("All faults are already embedded. Pipeline complete");
            return;
        }

        int totalEmbedded = 0;
        int totalFailed = 0;
        List<List<Fault>> batches = splitIntoBatches(toEmbed);

        log.info("Processing {} batches of up to {} faults each", batches.size(), BATCH_SIZE);

        for (int i = 0; i < batches.size(); i++) {
            List<Fault> batch = batches.get(i);
            log.info("Processing batch {}/{} ({} faults)...", i, batches.size(), batch.size());

            try {
                processBatch(batch);
                totalEmbedded += batch.size();
                log.info("Batch {}/{} complete. Total embedded so far: {}",
                        i+1, batch.size(), totalEmbedded);

                if (i < batches.size() - 1) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Pipeline interrupted at batch {}", i + 1);
                break;
            } catch (Exception e) {
                log.error("Batch {}/{} failed: {}. Skipping batch.", i + 1, batches.size(), e.getMessage());
                totalFailed += batch.size();
            }
        }

        long totalStored = faultEmbeddingRepository.count();
        log.info("Pipeline complete. Embedded: {}, failed: {}, Total in pgvector: {}",
                totalEmbedded, totalFailed, totalStored);
    }

    private void processBatch(List<Fault> batch) {

        List<String> texts = batch.stream()
                .map(EmbeddingUtils::buildEmbeddingText)
                .toList();

        List<float[]> embeddings = embeddingService.embedDocument(texts);

        if (embeddings.size() != batch.size()) {
            throw new RuntimeException(String.format("Voyage AI returned %d embeddings but batch had %d faults",
                    embeddings.size(), batch.size()));
        }

        List<String> faultIds = batch.stream()
                .map(Fault::getId)
                .toList();

        faultEmbeddingRepository.saveBatch(faultIds, embeddings);
    }

    private <T> List<List<T>> splitIntoBatches(List<T> items) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i+= BATCH_SIZE) {
            batches.add(items.subList(i, Math.min(i + BATCH_SIZE, items.size())));
        }
        return batches;
    }
}
