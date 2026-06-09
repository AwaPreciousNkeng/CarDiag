package com.codewithpcodes.cardiag.sync;

import com.codewithpcodes.cardiag.diagnosis.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisDetailDTO {

    private Integer id;
    private String  faultId;
    private String  faultName;
    private String  category;
    private String  description;
    private String  urgency;
    private List<String> causes;
    private List<String> symptoms;
    private List<String> repairTips;
    private String  inputType;
    private String  imageDescription;
    private String  audioTranscription;
    private String  userText;
    private String  llmReport;
    private Double  confidenceScore;
    private String  confidenceLabel;
    private Boolean isLowConfidence;
    private Boolean recognised;
    private OffsetDateTime createdAt;

    public static DiagnosisDetailDTO from(Diagnosis diagnosis) {
        boolean recognised = diagnosis.getMatchedFault() != null;

        DiagnosisDetailDTOBuilder builder = DiagnosisDetailDTO.builder()
                .id(diagnosis.getId())
                .inputType(diagnosis.getInputType().name())
                .imageDescription(diagnosis.getImageDescription())
                .audioTranscription(diagnosis.getAudioTranscription())
                .userText(diagnosis.getUserText())
                .llmReport(diagnosis.getLlmReport())
                .confidenceScore(diagnosis.getConfidence())
                .confidenceLabel(resolveLabel(diagnosis.getConfidence()))
                .isLowConfidence(diagnosis.getIsLowConfidence())
                .recognised(recognised)
                .createdAt(diagnosis.getCreatedAt());

        if (recognised) {
            var fault = diagnosis.getMatchedFault();
            builder
                    .faultId(fault.getId())
                    .faultName(fault.getName())
                    .category(fault.getCategory().name())
                    .description(fault.getDescription())
                    .urgency(fault.getUrgency().name())
                    .causes(fault.getCauses())
                    .symptoms(fault.getSymptoms())
                    .repairTips(fault.getRepairTips());
        }

        return builder.build();
    }

    private static String resolveLabel(Double score) {
        if (score == null)  return "NONE";
        if (score >= 0.90)  return "HIGH";
        if (score >= 0.75)  return "MEDIUM";
        return "LOW";
    }
}

