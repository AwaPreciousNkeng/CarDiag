package com.codewithpcodes.cardiag.diagnosis;

import org.springframework.web.multipart.MultipartFile;

public record DiagnosisRequest(
        InputType inputType,
        MultipartFile file,
        String text,
        String userId
) {
}
