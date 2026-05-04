package com.codewithpcodes.cardiag.fault;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "fault_embeddings")
public class FaultEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fault_id", nullable = false)
    private Fault fault;

    @Enumerated(EnumType.STRING)
    private EmbeddingModality modality;
    @Column(name = "embedding", columnDefinition = "vector(768)", nullable = false)
    private PGvector embedding;

    @Column(name = "model_version", nullable = false)
    private String modelVersion = "text-embedding-004";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
