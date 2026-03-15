package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.service.OfficeProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/office-projects")
@RequiredArgsConstructor
public class OfficeProjectController {

    private final OfficeProjectService officeProjectService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.create(body)));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String status) {
        var data = status != null ? officeProjectService.getByStatus(status) : officeProjectService.getAll();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.search(q)));
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<?> getByMember(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.getByMember(userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.update(id, body)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.updateStatus(id, status)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.addMember(id, body)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "data", officeProjectService.removeMember(id, userId)));
    }
}
