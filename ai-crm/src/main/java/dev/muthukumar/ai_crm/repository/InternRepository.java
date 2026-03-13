package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Intern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InternRepository extends JpaRepository<Intern, Long> {
    Optional<List<Intern>> findByDomain_Id(Long domainId);
}
