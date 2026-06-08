package com.codewithpcodes.cardiag.embedding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaultMatchDTO {

    private String faultId;
    private double distance;

    public double getSimilarityScore() {
        return 1.0 - distance;
    }

    public boolean isAboveThreshold() {
        return getSimilarityScore() >= 0.70;
    }
}
