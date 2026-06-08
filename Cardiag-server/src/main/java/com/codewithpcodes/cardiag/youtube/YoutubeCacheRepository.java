package com.codewithpcodes.cardiag.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface YoutubeCacheRepository extends JpaRepository<YoutubeCache, Integer> {
    /**
     * Finds all non-expired cached videos for a given fault.
     * This is checked first before hitting the YouTube API.
     */
    @Query("""
            SELECT y FROM YoutubeCache y
            WHERE y.fault.id = :faultId
            AND y.expiresAt > :now
            """)
    List<YoutubeCache> findValidCacheByFaultId(String faultId, OffsetDateTime now);

    /**
     * Deletes all cached entries for a fault — used before inserting fresh results.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM YoutubeCache y WHERE y.fault.id = :faultId")
    void deleteAllByFaultId(String faultId);

    /**
     * Cleans up all expired entries across the whole table.
     * Can be run periodically to keep the table lean.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM YoutubeCache y WHERE y.expiresAt <= :now")
    int deleteAllExpired(OffsetDateTime now);
}
