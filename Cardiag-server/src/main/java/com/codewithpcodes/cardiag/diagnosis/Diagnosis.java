package com.codewithpcodes.cardiag.diagnosis;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.user.User;
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
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false)
    private InputType inputType;

    private String audioTranscription;
    private String imageDescription;
    private String userText;
    private String combinedContext;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_fault_id")
    private Fault matchedFault;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false)
    private Double distance;

    private String llmReport;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isLowConfidence = false;

    private String rawInputHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

}
