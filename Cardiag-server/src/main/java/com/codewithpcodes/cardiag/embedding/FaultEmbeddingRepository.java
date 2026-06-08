package com.codewithpcodes.cardiag.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FaultEmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves a single fault embedding to the fault_embeddings table.
     * Uses INSERT ... ON CONFLICT DO UPDATE so this is safe to call
     * even if an embedding for this fault already exists — it will
     * simply overwrite it.
     *
     * @param faultId   the fault code ID (e.g. "B1200")
     * @param embedding the 1024-dimensional float vector from Voyage AI
     */
    public void save(String faultId, float[] embedding) {
        String vectorStr = EmbeddingUtils.toVectorString(embedding);

        jdbcTemplate.update("""
                INSERT INTO fault_embeddings (fault_id, embedding, created_at)
                VALUES (?, ?::vector, ?)
                ON CONFLICT (fault_id) DO UPDATE
                    SET embedding   = EXCLUDED.embedding,
                        created_at  = EXCLUDED.created_at
                """,
                faultId,
                vectorStr,
                Timestamp.valueOf(LocalDateTime.now())
        );
    }

    /**
     * Saves a batch of fault embeddings in one transaction.
     * Much faster than saving one by one for 6000+ records.
     *
     * @param faultIds   list of fault code IDs
     * @param embeddings corresponding list of embedding vectors (same order)
     */
    public void saveBatch(List<String> faultIds, List<float[]> embeddings) {
        if (faultIds.size() != embeddings.size()) {
            throw new IllegalArgumentException(
                    "faultIds and embeddings lists must be the same size"
            );
        }

        List<Object[]> batchArgs = new java.util.ArrayList<>();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (int i = 0; i < faultIds.size(); i++) {
            batchArgs.add(new Object[]{
                    faultIds.get(i),
                    EmbeddingUtils.toVectorString(embeddings.get(i)),
                    now
            });
        }

        jdbcTemplate.batchUpdate("""
                INSERT INTO fault_embeddings (fault_id, embedding, created_at)
                VALUES (?, ?::vector, ?)
                ON CONFLICT (fault_id) DO UPDATE
                    SET embedding   = EXCLUDED.embedding,
                        created_at  = EXCLUDED.created_at
                """,
                batchArgs
        );

        log.debug("Batch saved {} embeddings to pgvector", faultIds.size());
    }

    /**
     * Finds the top N most similar fault embeddings to the given query embedding.
     * Uses the <=> operator which computes cosine distance.
     * Cosine distance: 0.0 = identical, 2.0 = completely opposite.
     * The IVFFlat index on fault_embeddings makes this fast even for 6000+ vectors.
     *
     * @param queryEmbedding the user's input embedding (from Voyage AI embedQuery)
     * @param limit how many top matches to return (usually 3 to 5)
     * @return list of FaultMatchDTO sorted by distance (closest first)
     */
    public List<FaultMatchDTO> findTopMatches(float[] queryEmbedding, int limit) {
        String vectorStr = EmbeddingUtils.toVectorString(queryEmbedding);

        return jdbcTemplate.query("""
                SELECT  fault_id,
                        (embedding <=> ?::vector) AS distance
                FROM    fault_embeddings
                ORDER   BY distance ASC
                LIMIT   ?
                """,
                (rs, rowNum) -> new FaultMatchDTO(
                        rs.getString("fault_id"),
                        rs.getDouble("distance")
                ),
                vectorStr,
                limit
        );
    }

    /**
     * Checks if an embedding already exists for a given fault ID.
     * Used during the pipeline to skip already-embedded faults.
     */
    public boolean existsByFaultId(String faultId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM fault_embeddings WHERE fault_id = ?",
                Integer.class,
                faultId
        );
        return count != null && count > 0;
    }

    /**
     * Returns the total number of embeddings stored in pgvector.
     * Useful for logging pipeline progress.
     */
    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM fault_embeddings",
                Long.class
        );
        return count != null ? count : 0L;
    }

}
