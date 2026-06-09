package com.codewithpcodes.cardiag.sync;

import com.codewithpcodes.cardiag.diagnosis.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisHistoryDTO {

    private Integer id;
    private String  faultId;
    private String  faultName;
    private String  category;
    private String  urgency;
    private String  inputType;
    private Double  confidenceScore;
    private String  confidenceLabel;
    private Boolean isLowConfidence;
    private Boolean recognised;
    private OffsetDateTime createdAt;

    /**
     * Maps a Diagnosis entity to this DTO.
     * Called when building the history list response.
     */
    public static DiagnosisHistoryDTO from(Diagnosis diagnosis) {
        boolean recognised = diagnosis.getMatchedFault() != null;

        return DiagnosisHistoryDTO.builder()
                .id(diagnosis.getId())
                .faultId(recognised ? diagnosis.getMatchedFault().getId() : null)
                .faultName(recognised ? diagnosis.getMatchedFault().getName() : "Unrecognised")
                .category(recognised ? diagnosis.getMatchedFault().getCategory().name() : null)
                .urgency(recognised ? diagnosis.getMatchedFault().getUrgency().name() : null)
                .inputType(diagnosis.getInputType().name())
                .confidenceScore(diagnosis.getConfidence())
                .confidenceLabel(resolveLabel(diagnosis.getConfidence()))
                .isLowConfidence(diagnosis.getIsLowConfidence())
                .recognised(recognised)
                .createdAt(diagnosis.getCreatedAt())
                .build();
    }

    private static String resolveLabel(Double score) {
        if (score == null)      return "NONE";
        if (score >= 0.90)      return "HIGH";
        if (score >= 0.75)      return "MEDIUM";
        return "LOW";
    }
}
