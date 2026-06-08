package com.codewithpcodes.cardiag.diagnosis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Integer> {


    @Query("""
            SELECT d FROM Diagnosis d
            WHERE d.user.id = :userId
            ORDER BY d.createdAt DESC
            """)
    List<Diagnosis> findByUserIdOrderByCreatedAtDesc(Integer userId);

    boolean existsByRawInputHash(String rawInputHash);

    Optional<Diagnosis> findByRawInputHash(String rawInputHash);
}
