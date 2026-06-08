package com.codewithpcodes.cardiag.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;

    @GetMapping("/{faultId}")
    public ResponseEntity<List<VideoResult>> getVideosForFault(
            @PathVariable String faultId
    ) {
        List<VideoResult> videoResults = youtubeService.getVideosForFault(faultId);
        if (videoResults.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(videoResults);
    }
}
