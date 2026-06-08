package com.codewithpcodes.cardiag.diagnosis;

import com.codewithpcodes.cardiag.youtube.VideoResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisResponse {

    private String faultId;

    private String faultName;

    private String category;

    private String description;

    private String urgency;

    private List<String> causes;

    private List<String> symptoms;

    private List<String> repairTips;

    private double confidenceScore;

    private String confidenceLabel;

    private String llmReport;

    private String inputDescription;

    private List<VideoResult> videos;

    private boolean recognised;
    private String message;
}
