package com.codewithpcodes.cardiag.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbVersionDTO {
    private Integer version;
    private Long totalFaults;
    private Boolean updateAvailable;
}

