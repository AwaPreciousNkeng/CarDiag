package com.codewithpcodes.cardiag.embedding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FaultEmbeddingRepository extends JpaRepository<FaultEmbedding, Integer> {

    boolean existsByFaultId(String faultId);

    @Query(value = "SELECT fault_id FROM fault_embeddings", nativeQuery = true)
    Set<String> findAllEmbeddedFaultIds();

    Optional<FaultEmbedding> findByFaultId(String faultId);

    /**
     * Finds top N closest fault embeddings using cosine similarity.
     *
     * queryEmbedding must be passed as a String in pgvector format:
     * "[0.1, 0.2, 0.3, ...]"
     *
     * Returns Object[] rows where:
     *   [0] = fault_id (String)
     *   [1] = distance (Double)
     */
    @Query(value = """
            SELECT fe.fault_id,
                   (fe.embedding <=> CAST(:queryEmbedding AS vector)) AS distance
            FROM fault_embeddings fe
            ORDER BY distance ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopMatches(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("limit") int limit
    );

    /**
     * Inserts or updates an embedding for a fault.
     *
     * embedding must be passed as a String in pgvector format:
     * "[0.1, 0.2, 0.3, ...]"
     */
    @Modifying
    @Transactional // Safe execution boundary
    @Query(value = """
            INSERT INTO fault_embeddings (fault_id, modality, source_text, embedding, model_version, created_at)
            VALUES (:faultId, :modality, :sourceText, CAST(:embedding AS vector), :modelVersion, NOW())
            -- Include modality in the conflict target if a fault can have multiple embedding types
            ON CONFLICT (fault_id, modality) DO UPDATE\s
                SET embedding     = EXCLUDED.embedding,
                    source_text   = EXCLUDED.source_text,
                    model_version = EXCLUDED.model_version,
                    created_at    = NOW()
           \s""", nativeQuery = true)
    void upsertEmbedding(
            @Param("faultId") String faultId,                 // "P0300"
            @Param("modality") String modality,               // "TEXT_SCRAPED"
            @Param("sourceText") String sourceText,           // "Fault Code: P0300..."
            @Param("embedding") String embedding,             // "[0.039, -0.009...]"
            @Param("modelVersion") String modelVersion        // "voyage-3"
    );
}