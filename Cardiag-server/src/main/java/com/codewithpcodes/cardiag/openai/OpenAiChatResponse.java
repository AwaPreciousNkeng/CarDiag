package com.codewithpcodes.cardiag.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiChatResponse {

    @JsonProperty("choices")
    private List<ChoiceDTO> choices;

    /**
     * Convenience method — extracts the text content from the first choice.
     * This is what we actually care about — GPT-4o's description of the input.
     */
    public String getContent() {
        if (choices == null || choices.isEmpty()) return null;
        ChoiceDTO first = choices.getFirst();
        if (first.getMessage() == null) return null;
        return first.getMessage().getContent();
    }

    // ── Nested: one completion choice ────────────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChoiceDTO {

        @JsonProperty("message")
        private MessageDTO message;
    }

    // ── Nested: the message inside a choice ──────────────────

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageDTO {

        @JsonProperty("content")
        private String content;
    }
}

