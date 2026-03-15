package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.*;
import dev.muthukumar.ai_crm.exception.BadRequestException;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.*;
import dev.muthukumar.ai_crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REPLACE the existing AllocationService.java with this file.
 */
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepo;
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final InternRepository internRepo;
    private final ProjectRepository projectRepo;

    // ── Create ──────────────────────────────────────────────────────────────
    @Transactional
    public Allocation create(Map<String, Object> body) {

        Long studentId  = toLong(body.get("studentId"));
        String category = (String) body.get("category");
        String startDate = (String) body.get("startDate");
        String endDate   = body.get("endDate") != null ? (String) body.get("endDate") : null;
        String notes     = body.get("notes") != null ? (String) body.get("notes") : null;
        Object feeObj    = body.get("totalFee");

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        Allocation a = new Allocation();
        a.setStudent(student);
        a.setCategory(AllocationCategory.valueOf(category));
        a.setStartDate(LocalDate.parse(startDate));
        if (endDate != null) a.setEndDate(LocalDate.parse(endDate));
        if (notes != null) a.setNotes(notes);

        BigDecimal feeOverride = feeObj != null
                ? new BigDecimal(feeObj.toString()) : null;

        switch (AllocationCategory.valueOf(category)) {
            case COURSE -> {
                Long courseId = toLong(body.get("courseId"));
                if (courseId == null) throw new BadRequestException("courseId required for COURSE");
                Course course = courseRepo.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
                a.setCourse(course);
                BigDecimal fee = feeOverride != null ? feeOverride
                        : BigDecimal.valueOf(course.getAmount());
                a.setTotalFee(fee);
                a.setBalanceDue(fee);
                a.setCourseStatus(CourseStatus.ENROLLED);
            }
            case INTERN -> {
                Long internId = toLong(body.get("internProgramId"));
                if (internId == null) throw new BadRequestException("internProgramId required for INTERN");
                Intern intern = internRepo.findById(internId)
                        .orElseThrow(() -> new ResourceNotFoundException("Intern", internId));
                a.setIntern(intern);
                BigDecimal fee = feeOverride != null ? feeOverride
                        : BigDecimal.valueOf(intern.getAmount());
                a.setTotalFee(fee);
                a.setBalanceDue(fee);
                a.setInternStatus(InternStatus.ONGOING);
            }
            case PROJECT -> {
                Long projectId = toLong(body.get("studentProjectId"));
                if (projectId == null) throw new BadRequestException("studentProjectId required for PROJECT");
                Project project = projectRepo.findById(projectId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
                a.setProject(project);
                BigDecimal fee = feeOverride != null ? feeOverride
                        : BigDecimal.valueOf(project.getAmount());
                a.setTotalFee(fee);
                a.setBalanceDue(fee);
                a.setProjectStatus(ProjectStatus.NOT_STARTED);
            }
        }

        return allocationRepo.save(a);
    }

    // ── Update status ────────────────────────────────────────────────────────
    @Transactional
    public Allocation updateStatus(Long id, Map<String, String> body) {
        Allocation a = findById(id);

        if (body.get("allocationStatus") != null)
            a.setAllocationStatus(AllocationStatus.valueOf(body.get("allocationStatus")));

        switch (a.getCategory()) {
            case PROJECT -> { if (body.get("projectStatus") != null) a.setProjectStatus(ProjectStatus.valueOf(body.get("projectStatus"))); }
            case INTERN  -> { if (body.get("internStatus")  != null) a.setInternStatus(InternStatus.valueOf(body.get("internStatus"))); }
            case COURSE  -> { if (body.get("courseStatus")  != null) a.setCourseStatus(CourseStatus.valueOf(body.get("courseStatus"))); }
        }

        if (body.get("actualEndDate") != null)
            a.setActualEndDate(LocalDate.parse(body.get("actualEndDate")));
        if (body.get("notes") != null)
            a.setNotes(body.get("notes"));

        return allocationRepo.save(a);
    }

    // ── Payment update (called by PaymentService) ─────────────────────────
    @Transactional
    public void refreshPaymentTotals(Allocation a) {
        BigDecimal paid  = a.getAmountPaid() != null ? a.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal total = a.getTotalFee()   != null ? a.getTotalFee()   : BigDecimal.ZERO;
        a.setBalanceDue(total.subtract(paid));

        if (paid.compareTo(BigDecimal.ZERO) == 0)       a.setPaymentStatus(PaymentStatus.PENDING);
        else if (paid.compareTo(total) >= 0)             a.setPaymentStatus(PaymentStatus.PAID);
        else                                             a.setPaymentStatus(PaymentStatus.PARTIAL);

        allocationRepo.save(a);
    }

    // ── Queries ──────────────────────────────────────────────────────────────
    public List<Allocation> getAll()                           { return allocationRepo.findAll(); }
    public List<Allocation> getByStudent(Long sid)             { return allocationRepo.findByStudentId(sid); }
    public List<Allocation> getActiveByStudent(Long sid)       { return allocationRepo.findActiveByStudent(sid); }
    public List<Allocation> getByCategory(String cat)          { return allocationRepo.findByCategory(AllocationCategory.valueOf(cat)); }
    public List<Allocation> getPaidWithoutInvoice()            { return allocationRepo.findPaidWithoutInvoice(); }
    public List<Allocation> getCompletedWithoutCertificate()   { return allocationRepo.findCompletedWithoutCertificate(); }

    public Allocation findById(Long id) {
        return allocationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", id));
    }

    // ── Catalog dropdown resolver ────────────────────────────────────────────
    public List<?> getCatalogItems(String category, Long domainId) {
        return switch (AllocationCategory.valueOf(category)) {
            case COURSE  -> courseRepo.findByDomain_Id(domainId).orElse(List.of());
            case INTERN  -> internRepo.findByDomain_Id(domainId).orElse(List.of());
            case PROJECT -> projectRepo.findByDomain_Id(domainId).orElse(List.of());
        };
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        return Long.parseLong(o.toString());
    }
}
