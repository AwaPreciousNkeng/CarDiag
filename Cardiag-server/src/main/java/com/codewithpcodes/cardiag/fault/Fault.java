package com.codewithpcodes.cardiag.fault;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "faults")
public class Fault {

    @Id
    @Column(name = "id",  nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FaultCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false)
    private UrgencyLevel urgency;

    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "fault_causes", joinColumns = @JoinColumn(name = "fault_id"))
    @Column(name = "cause")
    private List<String> causes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "fault_symptoms", joinColumns = @JoinColumn(name = "fault_id"))
    @Column(name = "symptom")
    private List<String> symptoms;

    @Column(name = "repair_tips", nullable = false)
    private List<String> repairTips;

    @Enumerated(EnumType.STRING)
    @Column(name = "warning_light", nullable = false)
    private WarningLight warningLight;

    @Column(name = "sound_pattern")
    private String soundPattern;

    @Column(name = "source_url")
    private String sourceUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updateAt;

}
