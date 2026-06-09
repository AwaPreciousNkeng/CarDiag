package com.codewithpcodes.cardiag.sync;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "Sync Management Endpoints")
public class SyncController {

    private final SyncService syncService;


    @GetMapping("/version")
    public ResponseEntity<DbVersionDTO> checkVersion(
            @RequestParam(required = false) Integer clientVersion
    ) {
        log.debug("Version check request. clientVersion={}", clientVersion);
        return ResponseEntity.ok(syncService.checkVersion(clientVersion));
    }

    @GetMapping("/faultdb")
    public ResponseEntity<List<FaultSyncDTO>> getFaultDatabase() {
        log.info("Fault database download requested.");
        List<FaultSyncDTO> snapshot = syncService.getFaultSnapshot();

        if (snapshot.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(snapshot);
    }
}
