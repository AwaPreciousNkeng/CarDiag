package com.codewithpcodes.cardiag.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiChatRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<MessageDTO> messages;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    // ── Message ──────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MessageDTO {

        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private List<ContentDTO> content;
    }

    // ── Content block ─────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContentDTO {

        @JsonProperty("type")
        private String type;        // "text", "image_url", or "input_audio"

        @JsonProperty("text")
        private String text;        // used when type = "text"

        @JsonProperty("image_url")
        private ImageUrlDTO imageUrl; // used when type = "image_url"

        @JsonProperty("input_audio")
        private InputAudioDTO inputAudio; // used when type = "input_audio"
    }

    // ── Image URL block ───────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageUrlDTO {

        @JsonProperty("url")
        private String url;         // "data:image/jpeg;base64,{base64string}"
    }

    // ── Audio input block ─────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InputAudioDTO {

        @JsonProperty("data")
        private String data;        // raw base64 encoded audio bytes

        @JsonProperty("format")
        private String format;      // "wav" or "mp3"
    }
}

