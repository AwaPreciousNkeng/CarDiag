package com.codewithpcodes.cardiag.fault;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FaultRepository extends JpaRepository<Fault, String> {
}
