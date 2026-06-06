package com.codewithpcodes.cardiag.fault;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaultEmbeddingRepository extends JpaRepository<FaultEmbedding, Integer> {
//    @Query(value = "select * from search_faults_by_embedding(" +
//            "CAST(:embedding AS vector), " +
//            "CAST(:modality AS embedding_modality), " +
//            ":resultLimit " +
//            ":minConfidence " +
//            ")", nativeQuery = true)
//    List<Object[]> searchByEmbedding(
//            @Param("embedding") String embedding,
//            @Param("modality") String modality,
//            @Param("resultLimit") int resultLimit,
//            @Param("minConfidence") double minConfidence
//    );
    boolean existsByFaultIdAndModality(Integer faultID, EmbeddingModality modality);
}
