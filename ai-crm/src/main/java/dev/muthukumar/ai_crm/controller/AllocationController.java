package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Allocation;
import dev.muthukumar.ai_crm.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Allocation a = allocationService.create(body);
        return ResponseEntity.ok(Map.of("success", true, "message", "Allocated successfully", "data", a));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.findById(id)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.getByStudent(studentId)));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.getByEmployee(employeeId)));
    }

    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<?> getActiveByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.getActiveByStudent(studentId)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(Map.of("success", true, "data", allocationService.getByCategory(category)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Status updated",
                "data", allocationService.updateStatus(id, body)));
    }

    /** Update assigned employee and/or timing */
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<?> updateAssignment(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Assignment updated",
                "data", allocationService.updateEmployeeAndTiming(id, body)));
    }

    @GetMapping("/catalog/items")
    public ResponseEntity<?> getCatalogItems(@RequestParam String category,
                                             @RequestParam Long domainId) {
        return ResponseEntity.ok(Map.of("success", true, "data",
                allocationService.getCatalogItems(category, domainId)));
    }
}
