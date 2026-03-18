package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.dto.SalaryRequest;
import dev.muthukumar.ai_crm.model.Salary;
import dev.muthukumar.ai_crm.service.SalaryPdfService;
import dev.muthukumar.ai_crm.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService    salaryService;
    private final SalaryPdfService pdfService;

    /** All roles: get all salaries */
    @GetMapping
    public ResponseEntity<List<Salary>> getAll() {
        return ResponseEntity.ok(salaryService.getAll());
    }

    /** Get salary records for a specific employee */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Salary>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getByEmployee(employeeId));
    }

    /** SUPER_ADMIN / ADMIN only: create salary record */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Salary> create(@RequestBody SalaryRequest req) {
        return ResponseEntity.ok(salaryService.create(req));
    }

    /** SUPER_ADMIN / ADMIN only: mark as PAID */
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Salary> markPaid(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.markPaid(id));
    }

    /** Generate payslip HTML (for browser print-to-PDF) */
    @GetMapping(value = "/{id}/payslip", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> payslip(@PathVariable Long id) {
        Salary s = salaryService.getById(id);
        return ResponseEntity.ok(pdfService.generatePayslipHtml(s));
    }

    /** Delete salary record (SUPER_ADMIN only) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        salaryService.getById(id); // throws if not found
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}
