package com.codewithpcodes.cardiag.openai;

import com.codewithpcodes.cardiag.embedding.EmbeddingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

/**
 * @author pcodes
 * Service that communicates with OpenAI's API for three tasks:
 * 1. describeImage()   → sends dashboard photo to GPT-4o Vision
 *                        returns a text description of warning lights
 * 2. describeAudio()   → sends engine recording to GPT-4o Audio
 *                        returns a text description of the sound
 * 3. embedDocuments()  → embeds fault text for storage in pgvector
 * 4. embedQuery()      → embeds user input text for similarity search
 * All four use the same OpenAI WebClient bean.
 * Image and audio go to /v1/chat/completions
 * Embeddings go to /v1/embeddings
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient openAiWebClient;

    @Value("${application.openai.api.vision-model}")
    private String visionModel;

    @Value("${application.openai.api.audio-model}")
    private String audioModel;

    @Value("${application.openai.api.embedding-model}")
    private String embeddingModel;

    @Value("${application.openai.api.max-token}")
    private int maxTokens;

    private static final int EMBEDDING_DIMENSIONS = 1024;


    /**
     * Sends a dashboard image to GPT-4o Vision and gets back a
     * plain English description of all visible warning lights.
     * The description is then embedded by embedQuery() and used
     * to search pgvector for matching faults.
     *
     * @param imageFile the dashboard photo uploaded by the user
     * @return text description of warning lights visible in the image
     */
    public String describeImage(MultipartFile imageFile) {
        log.debug("Sending image to GPT-4o Vision for description...");

        String base64Image = encodeToBase64(imageFile);
        String mediaType = resolveImageMediaType(imageFile.getOriginalFilename());

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model(visionModel)
                .maxTokens(maxTokens)
                .messages(List.of(
                        OpenAiChatRequest.MessageDTO.builder()
                                .role("user")
                                .content(List.of(
                                        // Text prompt first
                                        OpenAiChatRequest.ContentDTO.builder()
                                                .type("text")
                                                .text(buildImagePrompt())
                                                .build(),
                                        // Then the image
                                        OpenAiChatRequest.ContentDTO.builder()
                                                .type("image_url")
                                                .imageUrl(OpenAiChatRequest.ImageUrlDTO.builder()
                                                        .url("data:" + mediaType + ";base64," + base64Image)
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        return callChatCompletions(request, "image description");
    }


    /**
     * Sends an engine audio recording to GPT-4o Audio and gets back
     * a technical description of the sounds heard.
     * The description is then embedded and used to search pgvector
     * for matching fault records.
     *
     * @param audioFile the engine recording uploaded by the user
     * @return text description of the engine sound
     */
    public String describeAudio(MultipartFile audioFile) {
        log.debug("Sending audio to GPT-4o Audio for description...");

        String base64Audio = encodeToBase64(audioFile);
        String audioFormat = resolveAudioFormat(audioFile.getOriginalFilename());

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model(audioModel)
                .maxTokens(maxTokens)
                .messages(List.of(
                        OpenAiChatRequest.MessageDTO.builder()
                                .role("user")
                                .content(List.of(
                                        // Text prompt first
                                        OpenAiChatRequest.ContentDTO.builder()
                                                .type("text")
                                                .text(buildAudioPrompt())
                                                .build(),
                                        // Then the audio
                                        OpenAiChatRequest.ContentDTO.builder()
                                                .type("input_audio")
                                                .inputAudio(OpenAiChatRequest.InputAudioDTO.builder()
                                                        .data(base64Audio)
                                                        .format(audioFormat)
                                                        .build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        return callChatCompletions(request, "audio description");
    }

    /**
     * Sends the matched fault context and the user's input description
     * to GPT-4o and receives a rich, human-friendly diagnosis report.
     * This is the final AI step in the pipeline. pgvector finds the
     * closest fault — GPT-4o explains it to the user in plain language.
     *
     * @param inputDescription  what GPT-4o said when it described the image or audio
     * @param context the matched fault's full data from PostgreSQL
     * @return natural language report stored as llmReport in the Diagnosis entity
     */
    public String generateDiagnosisReport(String inputDescription, FaultContext context) {
        log.debug("Generating LLM diagnosis report for fault '{}'", context.faultId());

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model(visionModel)
                .maxTokens(maxTokens)
                .messages(List.of(
                        OpenAiChatRequest.MessageDTO.builder()
                                .role("user")
                                .content(List.of(
                                        OpenAiChatRequest.ContentDTO.builder()
                                                .type("text")
                                                .text(buildDiagnosisPrompt(inputDescription, context))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        return callChatCompletions(request, "diagnosis report");
    }


    /**
     * Embeds a single query text for similarity search at runtime.
     * Use when a user submits a description for diagnosis.
     *
     * @param text the description to embed
     * @return float[] vector
     */
    public float[] embedQuery(String text) {
        log.debug("Embedding query text via OpenAI...");
        List<float[]> results = callEmbeddingsApi(List.of(text));
        if (results.isEmpty()) {
            throw new RuntimeException("OpenAI returned empty embedding for query");
        }
        return results.getFirst();
    }

    /**
     * Calls /v1/chat/completions and extracts the text content from the response.
     */
    private String callChatCompletions(OpenAiChatRequest request, String context) {
        try {
            OpenAiChatResponse response = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiChatResponse.class)
                    .block();

            if (response == null || response.getContent() == null) {
                throw new RuntimeException("OpenAI returned null response for " + context);
            }

            log.debug("OpenAI {} complete. Response length: {} chars",
                    context, response.getContent().length());

            return response.getContent();

        } catch (WebClientResponseException e) {
            log.error("OpenAI API error for {}: status={}, body={}",
                    context, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API call failed for " + context + ": " + e.getMessage(), e);
        }
    }

    /**
     * Calls /v1/embeddings and returns a list of float[] vectors.
     * Results are sorted by index to guarantee order matches input order.
     */
    private List<float[]> callEmbeddingsApi(List<String> texts) {
        OpenAiEmbeddingRequest request = OpenAiEmbeddingRequest.builder()
                .input(texts)
                .model(embeddingModel)
                .dimensions(EMBEDDING_DIMENSIONS)
                .build();

        try {
            OpenAiEmbeddingResponse response = openAiWebClient.post()
                    .uri("/v1/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiEmbeddingResponse.class)
                    .block();

            if (response == null || response.getData() == null) {
                throw new RuntimeException("OpenAI returned null embedding response");
            }

            if (response.getUsage() != null) {
                log.debug("OpenAI embedding tokens used: {}", response.getUsage().getTotalTokens());
            }

            // Sort by index and convert List<Double> to float[]
            return response.getData()
                    .stream()
                    .sorted(Comparator.comparingInt(OpenAiEmbeddingResponse.EmbeddingDataDTO::getIndex))
                    .map(data -> EmbeddingUtils.toFloatArray(data.getEmbedding()))
                    .toList();

        } catch (WebClientResponseException e) {
            log.error("OpenAI Embedding API error: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI Embedding API call failed: " + e.getMessage(), e);
        }
    }


    /**
     * Encodes a MultipartFile to a base64 string.
     */
    private String encodeToBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode file to base64: " + e.getMessage(), e);
        }
    }

    /**
     * Resolves the MIME type of the image from the file name.
     * Defaults to image/jpeg if not recognizable.
     */
    private String resolveImageMediaType(String filename) {
        if (filename == null) return "image/jpeg";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    /**
     * Resolves the audio format from the file name.
     * Defaults to wav if not recognizable.
     * OpenAI supports: wav, mp3, m4a, ogg, flac, webm.
     */
    private String resolveAudioFormat(String filename) {
        if (filename == null) return "wav";
        if (filename.endsWith(".mp3")) return "mp3";
        if (filename.endsWith(".m4a")) return "m4a";
        if (filename.endsWith(".ogg")) return "ogg";
        if (filename.endsWith(".flac")) return "flac";
        if (filename.endsWith(".webm")) return "webm";
        return "wav";
    }


    /**
     * Prompt sent to GPT-4o when describing a dashboard image.
     * Instructs the model to be specific and technical so the
     * resulting text embeds well against our fault database.
     */
    private String buildImagePrompt() {
        return """
                You are a car diagnostic expert. Examine this dashboard image carefully.
                Identify and describe every warning light or indicator that is illuminated.
                For each warning light you can see, describe:
                - Its name or symbol (e.g. battery, oil pressure, engine temperature)
                - Its color
                - What vehicle system it relates to
                - What problem it typically indicates
                Be specific and technical. If no warning lights are visible, say so clearly.
                """;
    }

    /**
     * Prompt sent to GPT-4o when describing an engine audio recording.
     * Instructs the model to describe the mechanical sound in terms
     * that will match well against our fault database embeddings.
     */
    private String buildAudioPrompt() {
        return """
                You are an experienced automotive mechanic. Listen carefully to this engine recording.
                Describe the sounds you hear in technical detail, including:
                - The type of sound (knocking, squealing, hissing, rattling, grinding, ticking, whining)
                - The rhythm or pattern (constant, intermittent, rhythmic, random)
                - The intensity (loud, faint, getting worse)
                - Which part of the engine or vehicle it likely comes from
                - What mechanical fault or problem it most likely indicates
                Be specific and technical. Your description will be used to match against known fault patterns.
                """;
    }

    /**
     * Builds the final diagnosis prompt sent to GPT-4o.
     * We give GPT-4o two things:
     * 1. What the user submitted (input description)
     * 2. What our database found (matched fault context)
     * GPT-4o then generates a clear, plain-language report
     * the driver can actually understand and act on.
     */
    private String buildDiagnosisPrompt(String inputDescription, FaultContext ctx) {
        return String.format("""
                You are a professional automotive diagnostic assistant helping a car owner
                understand a vehicle problem.
 
                WHAT THE USER SUBMITTED:
                "%s"
 
                MATCHED FAULT FROM DATABASE:
                - Fault Code:   %s
                - Name:         %s
                - Category:     %s
                - Description:  %s
                - Urgency:      %s
                - Causes:       %s
                - Symptoms:     %s
                - Repair Tips:  %s
 
                Based on the above, write a clear and friendly diagnosis report for the car owner.
                Structure your report as follows:
 
                1. WHAT IS WRONG — explain the fault in simple, non-technical language
                2. WHY IT IS HAPPENING — explain the most likely causes
                3. HOW URGENT IS THIS — tell them what happens if they ignore it
                4. WHAT TO DO RIGHT NOW — immediate action the driver should take
                5. HOW TO FIX IT — brief explanation of the repair process
 
                Keep it conversational and easy to understand.
                Do not use jargon. Write as if explaining to someone with no mechanical knowledge.
                """,
                inputDescription,
                ctx.faultId(),
                ctx.faultName(),
                ctx.category(),
                ctx.description(),
                ctx.urgency(),
                String.join(", ", ctx.causes()),
                String.join(", ", ctx.symptoms()),
                String.join(", ", ctx.repairTips())
        );
    }
}

