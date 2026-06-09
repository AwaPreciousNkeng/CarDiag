package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final FaultRepository faultRepository;
    private final FaultEmbeddingRepository faultEmbeddingRepository;

    private static final double CONFIDENCE_THRESHOLD = 0.70;

    @Getter
    private double lastConfidenceScore = 0.0;

    @Getter
    private double lastDistance = 0.0;

    public Optional<Fault> findBestMatch(float[] queryEmbedding) {
        List<FaultMatchDTO> matches = searchPgVector(queryEmbedding, 1); // fix — use helper

        if (matches.isEmpty()) {
            lastConfidenceScore = 0.0;
            lastDistance        = 0.0;
            log.warn("pgvector returned no results for query embedding");
            return Optional.empty();
        }

        FaultMatchDTO best = matches.getFirst();
        lastConfidenceScore = best.getSimilarityScore();
        lastDistance        = best.getDistance();

        log.debug("Best match: faultId='{}', similarity={}",
                best.getFaultId(), best.getSimilarityScore());

        if (!best.isAboveThreshold()) {
            log.warn("Best match '{}' below confidence threshold ({} < {})",
                    best.getFaultId(), lastConfidenceScore, CONFIDENCE_THRESHOLD);
            return Optional.empty();
        }

        return faultRepository.findById(best.getFaultId());
    }

    public List<FaultMatchDTO> findTopMatches(float[] queryEmbedding, int limit) {
        List<FaultMatchDTO> matches = searchPgVector(queryEmbedding, limit); // fix — use helper

        List<FaultMatchDTO> filtered = matches.stream()
                .filter(FaultMatchDTO::isAboveThreshold)
                .toList();

        log.debug("pgvector returned {} matches, {} above threshold",
                matches.size(), filtered.size());

        return filtered;
    }

    /**
     * Converts float[] to String, calls the repository native query,
     * then parses the Object[] rows back into FaultMatchDTO objects.
     *
     * This is necessary because:
     * - JPA native queries cannot bind float[] as a parameter
     * - The repository returns Object[] not FaultMatchDTO directly
     */
    private List<FaultMatchDTO> searchPgVector(float[] queryEmbedding, int limit) {
        String vectorStr = EmbeddingUtils.toVectorString(queryEmbedding);

        try {
            List<Object[]> rows = faultEmbeddingRepository.findTopMatches(vectorStr, limit);

            return rows.stream()
                    .map(row -> new FaultMatchDTO(
                            (String) row[0],                        // fault_id
                            ((Number) row[1]).doubleValue()         // distance
                    ))
                    .toList();

        } catch (Exception e) {
            log.error("pgvector search failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}