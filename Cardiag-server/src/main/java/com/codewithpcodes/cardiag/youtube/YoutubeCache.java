package com.codewithpcodes.cardiag.youtube;

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
@Table(name = "youtube_cache")
public class YoutubeCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fault_id", nullable = false)
    private Fault fault;

    @Column(nullable = false)
    private String videoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String channelName;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @CreationTimestamp
    @Column(name = "cached_at", nullable = false, updatable = false)
    private OffsetDateTime cachedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}
