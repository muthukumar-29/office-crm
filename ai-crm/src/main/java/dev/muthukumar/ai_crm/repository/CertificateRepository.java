package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByAllocationId(Long allocationId);
    boolean existsByAllocationId(Long allocationId);
}
