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
public class OpenAiEmbeddingRequest {

    @JsonProperty("input")
    private List<String> input;

    @JsonProperty("model")
    private String model;

    @JsonProperty("dimensions")
    private Integer dimensions;
}