package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.OfficeProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OfficeProjectMemberRepository extends JpaRepository<OfficeProjectMember, Long> {
    List<OfficeProjectMember> findByOfficeProjectId(Long projectId);
    List<OfficeProjectMember> findByOfficeProjectIdAndIsActiveTrue(Long projectId);
    Optional<OfficeProjectMember> findByOfficeProjectIdAndUserId(Long projectId, Long userId);
}
