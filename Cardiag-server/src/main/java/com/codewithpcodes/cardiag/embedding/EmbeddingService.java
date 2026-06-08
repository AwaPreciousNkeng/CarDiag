package com.codewithpcodes.cardiag.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final WebClient voyageWebClient;

    @Value("${application.voyage.ai.model}")
    private String model;


//    public List<float[]> embedDocument(List<String> texts) {
//        return callVoyageApi(texts, "document");
//    }

    public float[] embedQuery(String queryText) {
        List<float[]> results = callVoyageApi(List.of(queryText), "query");
        if (results.isEmpty()) {
            throw new RuntimeException("Voyage AI returned empty embedding for query");
        }
        return results.getFirst();
    }

    private List<float[]> callVoyageApi(List<String> texts, String inputType) {
        Map<String, Object> requestBody = Map.of(
                "input", texts,
                "model", model,
                "input_type", inputType
        );

        JsonNode response = voyageWebClient.post()
                .uri("v1/embeddings")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("data")) {
            throw new RuntimeException("Invalid or empty response from Voyage AI");
        }

        JsonNode dataArray = response.get("data");
        List<float[]> embeddings = new ArrayList<>();

        for (JsonNode item : dataArray) {
            JsonNode embeddingNode = item.get("embedding");

            List<Double> doubles = StreamSupport
                    .stream(embeddingNode.spliterator(), false)
                    .map(JsonNode::asDouble)
                    .collect(Collectors.toList());

            embeddings.add(EmbeddingUtils.toFloatArray(doubles));
        }

        log.debug("Voyage AI returned {} embeddings for {} input texts",
                embeddings.size(), texts.size());

        return embeddings;
    }

}
