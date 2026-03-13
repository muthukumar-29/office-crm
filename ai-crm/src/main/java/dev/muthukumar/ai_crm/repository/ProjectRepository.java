package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Domain;
import dev.muthukumar.ai_crm.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<List<Project>> findByDomain_Id(Long domainId);
}
