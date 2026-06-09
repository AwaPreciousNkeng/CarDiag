package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingPipelineService {

    private final FaultRepository faultRepository;
    private final FaultEmbeddingRepository faultEmbeddingRepository;
    private final EmbeddingService embeddingService;

    // Increased batch size to reduce network overhead (ensure this fits Voyage AI token limits)
    private static final int BATCH_SIZE   = 20;

    // Base delay is now minimal, only meant to prevent instantly flooding the network
    private static final long BASE_DELAY_MS = 100;
    private static final int MAX_RETRIES  = 5;
    private static final String MODEL       = "voyage-3";
    private static final String MODALITY    = "TEXT";

    public void runPipeline() {
        log.info("Starting optimized embedding pipeline...");

        List<Fault> allFaults = faultRepository.findAll();
        Set<String> existingIds = faultEmbeddingRepository.findAllEmbeddedFaultIds();

        List<Fault> toEmbed = allFaults.stream()
                .filter(f -> !existingIds.contains(f.getId()))
                .toList();

        if (toEmbed.isEmpty()) {
            log.info("All faults already embedded. Pipeline complete.");
            return;
        }

        int totalEmbedded = 0;
        int totalFailed   = 0;
        List<List<Fault>> batches = splitIntoBatches(toEmbed);

        log.info("Processing {} batches of {} faults each.", batches.size(), BATCH_SIZE);

        // FIX: Declare backoff OUTSIDE the loop so the penalty carries over if the API is angry
        long currentBackoff = 2000;

        for (int i = 0; i < batches.size(); i++) {
            List<Fault> batch = batches.get(i);
            boolean success  = false;
            int attempts     = 0;

            while (!success && attempts < MAX_RETRIES) {
                try {
                    processBatch(batch);
                    totalEmbedded += batch.size();
                    success = true;

                    // FIX: Only reset the backoff to 2 seconds AFTER a successful API call
                    currentBackoff = 2000;

                    log.info("Batch {}/{} complete. Total embedded: {}", i + 1, batches.size(), totalEmbedded);

                } catch (Exception e) {
                    attempts++;
                    boolean isRateLimit = e.getMessage().contains("429") || e.getMessage().contains("Too Many Requests");

                    log.warn("Batch {}/{} attempt {}/{} failed. Reason: {}",
                            i + 1, batches.size(), attempts, MAX_RETRIES, e.getMessage());

                    if (attempts < MAX_RETRIES) {
                        try {
                            long waitTime = isRateLimit ? currentBackoff * 2 : currentBackoff;
                            log.info("Backing off for {}ms before retrying...", waitTime);
                            Thread.sleep(waitTime);

                            // Save the increased penalty time for the next attempt/batch
                            currentBackoff = waitTime;
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        log.error("Batch {} permanently failed after {} attempts.", i + 1, MAX_RETRIES);
                        totalFailed += batch.size();

                        // If we completely fail a batch, force a massive delay before trying the next batch
                        // to allow the Voyage AI token bucket to completely refill.
                        try {
                            log.info("Taking a 60-second cooldown before attempting the next batch...");
                            Thread.sleep(60000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            if (i < batches.size() - 1 && success) {
                try { Thread.sleep(BASE_DELAY_MS); } catch (InterruptedException ignored) {}
            }
        }

        log.info("Pipeline complete. Embedded: {}, Failed: {}", totalEmbedded, totalFailed);
    }

    private void processBatch(List<Fault> batch) {
        List<String> texts = batch.stream()
                .map(EmbeddingUtils::buildEmbeddingText)
                .toList();

        // 1 API call per 50 items instead of 1 call per 10 items
        List<float[]> embeddings = embeddingService.embedDocument(texts);

        if (embeddings.size() != batch.size()) {
            throw new RuntimeException(String.format(
                    "Voyage AI returned %d embeddings but batch had %d faults",
                    embeddings.size(), batch.size()
            ));
        }

        // Save records
        for (int i = 0; i < batch.size(); i++) {
            Fault fault      = batch.get(i);
            String sourceText = texts.get(i);
            String vectorStr  = EmbeddingUtils.toVectorString(embeddings.get(i));

            faultEmbeddingRepository.upsertEmbedding(
                    fault.getId(),
                    MODALITY,
                    sourceText,
                    vectorStr,
                    MODEL
            );
        }
    }

    private <T> List<List<T>> splitIntoBatches(List<T> items) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += BATCH_SIZE) {
            batches.add(items.subList(i, Math.min(i + BATCH_SIZE, items.size())));
        }
        return batches;
    }
}