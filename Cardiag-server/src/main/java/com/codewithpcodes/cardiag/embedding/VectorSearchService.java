package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * Finds the single best matching fault for a given query embedding.
     * Returns empty if no match meets the confidence threshold —
     * this prevents showing the user a wrong diagnosis.
     *
     * @param queryEmbedding the float[] vector from OpenAI
     * @return the best matching fault, or empty if confidence is too low
     */
    public Optional<Fault> findBestMatch(float[] queryEmbedding) {
        List<FaultMatchDTO> matches = faultEmbeddingRepository.findTopMatches(queryEmbedding, 1);

        if (matches.isEmpty()) {
            lastConfidenceScore = 0.0;
            lastDistance = 0.0;
            log.warn("pgvector returned no results for query embedding");
            return Optional.empty();
        }

        FaultMatchDTO best = matches.getFirst();
        lastConfidenceScore = best.getSimilarityScore();
        lastDistance = best.getDistance();

        log.debug("Best match: faultId='{}', similarity={}",
                best.getFaultId(), best.getSimilarityScore());

        if (!best.isAboveThreshold()) {
            log.warn("Best match '{}' below confidence threshold ({} < {})",
                    best.getFaultId(), lastConfidenceScore, CONFIDENCE_THRESHOLD);
            return Optional.empty();
        }

        return faultRepository.findById(best.getFaultId());
    }

    /**
     * Finds the top N closest matching faults for a given query embedding.
     * Returns only matches that meet the confidence threshold.
     * Useful when you want to show the user multiple possible faults.
     * @param queryEmbedding the float[] vector from OpenAI
     * @param limit          how many results to return
     * @return list of FaultMatchDTO sorted by similarity (highest first)
     */
    public List<FaultMatchDTO> findTopMatches(float[] queryEmbedding, int limit) {
        List<FaultMatchDTO> matches = faultEmbeddingRepository.findTopMatches(queryEmbedding, limit);

        List<FaultMatchDTO> filtered = matches.stream()
                .filter(FaultMatchDTO::isAboveThreshold)
                .toList();

        log.debug("pgvector returned {} matches, {} above threshold",
                matches.size(), filtered.size());

        return filtered;
    }

}

