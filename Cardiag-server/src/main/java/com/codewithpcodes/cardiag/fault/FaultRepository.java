package com.codewithpcodes.cardiag.fault;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaultRepository extends JpaRepository<Fault, Integer> {
    Optional<Fault> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
