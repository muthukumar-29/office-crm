package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}
