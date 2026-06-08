package com.codewithpcodes.cardiag.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiEmbeddingResponse {

    @JsonProperty("data")
    private List<EmbeddingDataDTO> data;

    @JsonProperty("usage")
    private UsageDTO usage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingDataDTO {

        @JsonProperty("embedding")
        private List<Double> embedding;

        @JsonProperty("index")
        private int index;
    }

    // ── Nested: token usage ───────────────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageDTO {

        @JsonProperty("total_tokens")
        private int totalTokens;
    }
}

