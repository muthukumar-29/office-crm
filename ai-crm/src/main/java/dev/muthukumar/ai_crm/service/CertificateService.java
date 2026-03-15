package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.AllocationStatus;
import dev.muthukumar.ai_crm.exception.BadRequestException;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.*;
import dev.muthukumar.ai_crm.repository.AllocationRepository;
import dev.muthukumar.ai_crm.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certRepo;
    private final AllocationRepository allocationRepo;
    private final AllocationService allocationService;

    // Preview data without saving
    public Map<String, Object> preview(Long allocationId) {
        Allocation a = allocationService.findById(allocationId);
        return buildPreviewMap(a);
    }

    @Transactional
    public Certificate issue(Map<String, Object> body) {
        Long allocationId = Long.parseLong(body.get("allocationId").toString());
        if (certRepo.existsByAllocationId(allocationId))
            throw new BadRequestException("Certificate already issued for this allocation");

        Allocation a = allocationService.findById(allocationId);

        boolean eligible = AllocationStatus.COMPLETED.equals(a.getAllocationStatus());
        if (!eligible) throw new BadRequestException(
                "Allocation must be COMPLETED before issuing certificate. Current: " + a.getAllocationStatus());

        Certificate cert = new Certificate();
        cert.setCertificateNumber(generateCertNumber(a));
        cert.setAllocation(a);
        cert.setStudentName(a.getStudent().getName());
        cert.setRollNo(a.getStudent().getRollNo());
        cert.setCollegeName(a.getStudent().getCollegeName());
        cert.setDepartment(a.getStudent().getDepartment());
        cert.setDomainName(resolveDomain(a));
        cert.setProgramTitle(resolveTitle(a));
        cert.setCategory(a.getCategory().name());
        cert.setStartDate(a.getStartDate());
        cert.setEndDate(a.getActualEndDate() != null ? a.getActualEndDate() : a.getEndDate());
        cert.setIssuedDate(body.get("issuedDate") != null
                ? LocalDate.parse((String) body.get("issuedDate")) : LocalDate.now());
        if (body.get("grade")   != null) cert.setGrade((String) body.get("grade"));
        if (body.get("remarks") != null) cert.setRemarks((String) body.get("remarks"));
        certRepo.save(cert);

        a.setCertificateIssued(true);
        allocationRepo.save(a);

        return cert;
    }

    public Certificate getByAllocation(Long allocationId) {
        return certRepo.findByAllocationId(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found for allocation: " + allocationId));
    }

    public List<Certificate> getAll() { return certRepo.findAll(); }

    private String resolveTitle(Allocation a) {
        return switch (a.getCategory()) {
            case COURSE  -> a.getCourse()  != null ? a.getCourse().getName()  : "";
            case INTERN  -> a.getIntern()  != null ? a.getIntern().getTitle() : "";
            case PROJECT -> a.getProject() != null ? a.getProject().getTitle(): "";
        };
    }

    private String resolveDomain(Allocation a) {
        return switch (a.getCategory()) {
            case COURSE  -> a.getCourse()  != null && a.getCourse().getDomain()  != null ? a.getCourse().getDomain().getName()  : "";
            case INTERN  -> a.getIntern()  != null && a.getIntern().getDomain()  != null ? a.getIntern().getDomain().getName()  : "";
            case PROJECT -> a.getProject() != null && a.getProject().getDomain() != null ? a.getProject().getDomain().getName() : "";
        };
    }

    private String generateCertNumber(Allocation a) {
        String prefix = switch (a.getCategory()) { case COURSE -> "CRS"; case INTERN -> "INT"; case PROJECT -> "PRJ"; };
        return prefix + "-" + LocalDate.now().getYear() + "-" + String.format("%05d", a.getId());
    }

    private Map<String, Object> buildPreviewMap(Allocation a) {
        return Map.of(
                "allocationId",  a.getId(),
                "studentName",   a.getStudent().getName(),
                "rollNo",        a.getStudent().getRollNo() != null ? a.getStudent().getRollNo() : "",
                "collegeName",   a.getStudent().getCollegeName() != null ? a.getStudent().getCollegeName() : "",
                "department",    a.getStudent().getDepartment() != null ? a.getStudent().getDepartment() : "",
                "domainName",    resolveDomain(a),
                "programTitle",  resolveTitle(a),
                "category",      a.getCategory().name(),
                "startDate",     a.getStartDate() != null ? a.getStartDate().toString() : "",
                "endDate",       a.getEndDate()   != null ? a.getEndDate().toString()   : ""
        );
    }
}
