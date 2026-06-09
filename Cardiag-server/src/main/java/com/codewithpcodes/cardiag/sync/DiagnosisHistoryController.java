package com.codewithpcodes.cardiag.sync;

import com.codewithpcodes.cardiag.diagnosis.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/diagnoses")
@RequiredArgsConstructor
public class DiagnosisHistoryController {

    private final DiagnosisRepository diagnosisRepository;

    @GetMapping
    public ResponseEntity<List<DiagnosisHistoryDTO>> getHistory(
            @RequestParam Integer userId
    ) {
        log.debug("Fetching diagnosis history for userId={}", userId);

        List<DiagnosisHistoryDTO> history = diagnosisRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(DiagnosisHistoryDTO::from)
                .toList();

        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDetailDTO> getDiagnosisById(
            @PathVariable Integer id
    ) {
        log.debug("Fetching diagnosis detail for id={}", id);

        return diagnosisRepository.findById(id)
                .map(DiagnosisDetailDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnosis(
            @PathVariable Integer id
    ) {
        if (!diagnosisRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        diagnosisRepository.deleteById(id);
        log.debug("Deleted diagnosis id={}", id);
        return ResponseEntity.noContent().build();
    }
}

