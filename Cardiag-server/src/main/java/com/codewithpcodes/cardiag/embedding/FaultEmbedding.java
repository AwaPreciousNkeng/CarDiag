package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;
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

    @Column(columnDefinition = "TEXT")
    private String sourceText;

    @Convert(converter = VectorConverter.class)
    @Column(name = "embedding", columnDefinition = "vector(1024)", nullable = false)
    private float[] embedding;

    @Column(name = "model_version")
    @Builder.Default
    private String modelVersion = "voyage-3";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
