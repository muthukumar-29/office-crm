package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
}
