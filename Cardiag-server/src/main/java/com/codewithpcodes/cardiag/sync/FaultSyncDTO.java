package com.codewithpcodes.cardiag.sync;

import com.codewithpcodes.cardiag.fault.Fault;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaultSyncDTO {

    private String id;
    private String name;
    private String category;
    private String description;
    private String urgency;
    private String warningLight;
    private List<String> causes;
    private List<String> symptoms;
    private List<String> repairTips;

    public static FaultSyncDTO from(Fault fault) {
        return FaultSyncDTO.builder()
                .id(fault.getId())
                .name(fault.getName())
                .category(fault.getCategory().name())
                .description(fault.getDescription())
                .urgency(fault.getUrgency().name())
                .warningLight(fault.getWarningLight() != null
                        ? fault.getWarningLight().name()
                        : null)
                .causes(fault.getCauses())
                .symptoms(fault.getSymptoms())
                .repairTips(fault.getRepairTips())
                .build();
    }
}
