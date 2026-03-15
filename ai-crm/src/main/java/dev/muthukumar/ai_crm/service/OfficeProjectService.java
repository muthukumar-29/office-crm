package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.OfficeProjectStatus;
import dev.muthukumar.ai_crm.exception.BadRequestException;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.*;
import dev.muthukumar.ai_crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OfficeProjectService {

    private final OfficeProjectRepository projectRepo;
    private final OfficeProjectMemberRepository memberRepo;
    private final UserRepository userRepo;

    public OfficeProject create(Map<String, Object> body) {
        OfficeProject p = new OfficeProject();
        fillProject(p, body);
        return projectRepo.save(p);
    }

    public OfficeProject update(Long id, Map<String, Object> body) {
        OfficeProject p = findById(id);
        fillProject(p, body);
        return projectRepo.save(p);
    }

    public OfficeProject updateStatus(Long id, String status) {
        OfficeProject p = findById(id);
        p.setStatus(OfficeProjectStatus.valueOf(status));
        if (OfficeProjectStatus.COMPLETED.name().equals(status))
            p.setActualCompletionDate(LocalDate.now());
        return projectRepo.save(p);
    }

    public List<OfficeProject> getAll()                     { return projectRepo.findAll(); }
    public List<OfficeProject> getByStatus(String s)        { return projectRepo.findByStatus(OfficeProjectStatus.valueOf(s)); }
    public List<OfficeProject> getByMember(Long userId)     { return projectRepo.findByMemberId(userId); }
    public List<OfficeProject> search(String q)             { return projectRepo.search(q); }

    @Transactional
    public OfficeProject addMember(Long projectId, Map<String, Object> body) {
        OfficeProject project = findById(projectId);
        Long userId = Long.parseLong(body.get("userId").toString());
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        memberRepo.findByOfficeProjectIdAndUserId(projectId, userId).ifPresent(m -> {
            if (Boolean.TRUE.equals(m.getIsActive()))
                throw new BadRequestException("User is already an active member");
            m.setIsActive(true);
            memberRepo.save(m);
        });

        if (memberRepo.findByOfficeProjectIdAndUserId(projectId, userId).isEmpty()) {
            OfficeProjectMember m = new OfficeProjectMember();
            m.setOfficeProject(project);
            m.setUser(user);
            if (body.get("role")       != null) m.setRole((String) body.get("role"));
            if (body.get("joinedDate") != null) m.setJoinedDate(LocalDate.parse((String) body.get("joinedDate")));
            else m.setJoinedDate(LocalDate.now());
            memberRepo.save(m);
        }

        return projectRepo.findById(projectId).orElseThrow();
    }

    @Transactional
    public OfficeProject removeMember(Long projectId, Long userId) {
        OfficeProjectMember m = memberRepo.findByOfficeProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in this project"));
        m.setIsActive(false);
        m.setLeftDate(LocalDate.now());
        memberRepo.save(m);
        return projectRepo.findById(projectId).orElseThrow();
    }

    public OfficeProject findById(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OfficeProject", id));
    }

    private void fillProject(OfficeProject p, Map<String, Object> body) {
        if (body.get("name")        != null) p.setName((String) body.get("name"));
        if (body.get("description") != null) p.setDescription((String) body.get("description"));
        if (body.get("clientName")  != null) p.setClientName((String) body.get("clientName"));
        if (body.get("clientContact") != null) p.setClientContact((String) body.get("clientContact"));
        if (body.get("techStack")   != null) p.setTechStack((String) body.get("techStack"));
        if (body.get("notes")       != null) p.setNotes((String) body.get("notes"));
        if (body.get("status")      != null) p.setStatus(OfficeProjectStatus.valueOf((String) body.get("status")));
        if (body.get("startDate")   != null) p.setStartDate(LocalDate.parse((String) body.get("startDate")));
        if (body.get("deadline")    != null) p.setDeadline(LocalDate.parse((String) body.get("deadline")));
        if (body.get("contractValue") != null) p.setContractValue(new BigDecimal(body.get("contractValue").toString()));
        if (body.get("projectManagerId") != null) {
            Long managerId = Long.parseLong(body.get("projectManagerId").toString());
            userRepo.findById(managerId).ifPresent(p::setProjectManager);
        }
    }
}
