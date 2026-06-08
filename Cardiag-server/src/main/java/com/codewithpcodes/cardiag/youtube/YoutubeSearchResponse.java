package com.codewithpcodes.cardiag.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeSearchResponse {

    @JsonProperty("items")
    private List<YoutubeItemDTO> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoutubeItemDTO {

        @JsonProperty("id")
        private YoutubeItemIdDTO id;

        @JsonProperty("snippet")
        private YoutubeSnippetDTO snippet;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoutubeItemIdDTO {

        @JsonProperty("videoId")
        private String videoId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoutubeSnippetDTO {

        @JsonProperty("title")
        private String title;

        @JsonProperty("channelTitle")
        private String channelTitle;

        @JsonProperty("thumbnails")
        private YoutubeThumbnailsDTO thumbnails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoutubeThumbnailsDTO {

        @JsonProperty("medium")
        private YoutubeThumbnailDTO medium;

        @JsonProperty("high")
        private YoutubeThumbnailDTO high;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoutubeThumbnailDTO {

        @JsonProperty("url")
        private String url;
    }
}
