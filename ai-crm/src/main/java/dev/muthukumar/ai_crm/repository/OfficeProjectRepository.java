package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.enums.OfficeProjectStatus;
import dev.muthukumar.ai_crm.model.OfficeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OfficeProjectRepository extends JpaRepository<OfficeProject, Long> {
    List<OfficeProject> findByStatus(OfficeProjectStatus status);

    @Query("SELECT op FROM OfficeProject op JOIN op.members m WHERE m.user.id = :uid AND m.isActive = true")
    List<OfficeProject> findByMemberId(@Param("uid") Long userId);

    @Query("SELECT op FROM OfficeProject op WHERE LOWER(op.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(op.clientName) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<OfficeProject> search(@Param("q") String q);
}
