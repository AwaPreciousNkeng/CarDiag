package com.codewithpcodes.cardiag.youtube;

import com.codewithpcodes.cardiag.fault.Fault;
import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class YoutubeService {

    private final YoutubeCacheRepository youtubeCacheRepository;
    private final FaultRepository faultRepository;
    private final WebClient youtubeWebClient;

    @Value("${application.youtube.api.results-per-fault}")
    private int maxResults;

    @Value("${application.youtube.api.cache-ttl-days}")
    private int cacheTtlDays;

    @Transactional
    public List<VideoResult> getVideosForFault(String faultId) {

        List<YoutubeCache> cached = youtubeCacheRepository
                .findValidCacheByFaultId(faultId, OffsetDateTime.now());

        if (!cached.isEmpty()) {
            log.debug("Cache hit for fault '{}'. Returning {} cached videos", faultId, cached.size());
            return cached.stream()
                    .map(VideoResult::toVideoResult)
                    .toList();
        }

        log.debug("Cache miss for fault '{}'. Calling YouTube API...", faultId);

        Fault fault = faultRepository.findById(faultId)
                .orElse(null);
        if (fault == null) {
            log.warn("Fault '{}' not found in database. Cannot fetch videos", faultId);
            return Collections.emptyList();
        }

        List<VideoResult> videos = fetchFromYoutubeApi(fault);

        if (videos.isEmpty()) {
            log.warn("YouTube API returned no results for fault '{}'", faultId);
            return Collections.emptyList();
        }

        saveToCache(fault, videos);
        return videos;
    }

    private List<VideoResult> fetchFromYoutubeApi(Fault fault) {
        String searchQuery = buildSearchQuery(fault);

        log.debug("YouTube API query: '{}'", searchQuery);

        try {
            YoutubeSearchResponse response = youtubeWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/youtube/v3/search")
                            .queryParam("part", "snippet")
                            .queryParam("q", searchQuery)
                            .queryParam("type", "video")
                            .queryParam("maxResults", maxResults)
                            .queryParam("relevanceLanguage", "en")
                            .queryParam("videoDuration", "medium") // 4–20 min
                            .build())
                    .retrieve()
                    .bodyToMono(YoutubeSearchResponse.class)
                    .block();

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return Collections.emptyList();
            }

            return response.getItems().stream()
                    .filter(item -> item.getId() != null && item.getId().getVideoId() != null)
                    .filter(item -> item.getSnippet() != null)
                    .map(VideoResult::toVideoResult)
                    .toList();

        } catch (Exception e) {
            log.error("YouTube API call failed for fault '{}': {}", fault.getId(), e.getMessage());
            return Collections.emptyList();
        }

    }

    private String buildSearchQuery(Fault fault) {
        return fault.getName() + " car repair fix how to";
    }

    private void saveToCache(Fault fault, List<VideoResult> videos) {
        youtubeCacheRepository.deleteAllByFaultId(fault.getId());

        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(cacheTtlDays);

        List<YoutubeCache> cacheEntries = videos.stream()
                .map(video -> YoutubeCache.builder()
                        .fault(fault)
                        .videoId(video.videoId())
                        .title(video.title())
                        .channelName(video.channelName())
                        .thumbnailUrl(video.thumbnailUrl())
                        .videoUrl(video.videoUrl())
                        .expiresAt(expiresAt)
                        .build()
                )
                .toList();

        youtubeCacheRepository.saveAll(cacheEntries);
        log.debug("cached {} videos for fault '{}' (expires {})",
                videos.size(), fault.getId(), expiresAt);
    }

    @Transactional
    public int evictExpiredCache() {
        int deleted = youtubeCacheRepository.deleteAllExpired(OffsetDateTime.now());
        log.info("Evicted {} expired cached videos", deleted);
        return deleted;
    }
}
