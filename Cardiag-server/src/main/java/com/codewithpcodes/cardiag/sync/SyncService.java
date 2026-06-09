package com.codewithpcodes.cardiag.sync;

import com.codewithpcodes.cardiag.fault.FaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final FaultRepository faultRepository;

    @Value("${application.sync.db-version}")
    private Integer currentDbVersion;

    public DbVersionDTO checkVersion(Integer clientVersion) {
        long totalFaults = faultRepository.count();
        boolean updateAvailable = clientVersion == null || currentDbVersion < clientVersion;

        log.debug("Version check. serverVersion={}, clientVersion={}, updateAvailable={}",
                currentDbVersion, clientVersion, updateAvailable);

        return DbVersionDTO.builder()
                .version(currentDbVersion)
                .totalFaults(totalFaults)
                .updateAvailable(updateAvailable)
                .build();
    }

    public List<FaultSyncDTO> getFaultSnapshot() {
        log.info("Building fault snapshot for mobile sync...");

        List<FaultSyncDTO> snapshot = faultRepository.findAll()
                .stream()
                .map(FaultSyncDTO::from)
                .toList();

        log.info("Fault snapshot built. {} records.", snapshot.size());
        return snapshot;
    }
}
