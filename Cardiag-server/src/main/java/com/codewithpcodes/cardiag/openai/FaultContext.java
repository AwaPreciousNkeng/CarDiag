package com.codewithpcodes.cardiag.openai;

import java.util.List;

public record FaultContext(
        String faultId,
        String faultName,
        String category,
        String description,
        String urgency,
        List<String> causes,
        List<String> symptoms,
        List<String> repairTips
) {
}
