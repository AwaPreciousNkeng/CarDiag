package com.codewithpcodes.cardiag.embedding;

import com.codewithpcodes.cardiag.fault.Fault;

import java.util.List;

public class EmbeddingUtils {

    private EmbeddingUtils() {}

    /**
     * Converts a float[] embedding to the string format pgvector expects.
     * Example output: "[0.12345, -0.45678, 0.98765, ...]"
     */
    public static String toVectorString(float[] embedding) {
        if (embedding == null || embedding.length == 0) {
            throw new IllegalArgumentException("Embedding array cannot be null or empty");
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Converts a List<Double> (from Voyage API JSON response) to float[].
     * Voyage API returns embeddings as List<Double>, we store them as float[].
     */
    public static float[] toFloatArray(List<Double> doubles) {
        if (doubles == null || doubles.isEmpty()) {
            throw new IllegalArgumentException("Embedding list cannot be null or empty");
        }

        float[] result = new float[doubles.size()];
        for (int i = 0; i < doubles.size(); i++) {
            result[i] = doubles.get(i).floatValue();
        }
        return result;
    }

    /**
     * Builds a rich text representation of a fault for embedding.
     * We concatenate all meaningful fields so the embedding captures
     * the full semantic meaning of the fault — not just the description.
     * This text is what gets sent to Voyage AI for embedding.
     */
    public static String buildEmbeddingText(Fault fault) {
        StringBuilder sb = new StringBuilder();

        sb.append("Fault Code: ").append(fault.getId()).append(". ");
        sb.append("Name: ").append(fault.getName()).append(". ");
        sb.append("Category: ").append(fault.getCategory()).append(". ");

        if (fault.getDescription() != null && !fault.getDescription().isBlank()) {
            sb.append("Description: ").append(fault.getDescription()).append(". ");
        }

        if (fault.getSymptoms() != null && !fault.getSymptoms().isEmpty()) {
            sb.append("Symptoms: ")
                    .append(String.join(", ", fault.getSymptoms()))
                    .append(". ");
        }

        if (fault.getCauses() != null && !fault.getCauses().isEmpty()) {
            sb.append("Causes: ")
                    .append(String.join(", ", fault.getCauses()))
                    .append(". ");
        }

        if (fault.getRepairTips() != null && !fault.getRepairTips().isEmpty()) {
            sb.append("Repair Tips: ")
                    .append(String.join(", ", fault.getRepairTips()))
                    .append(".");
        }

        return sb.toString().trim();
    }
}
