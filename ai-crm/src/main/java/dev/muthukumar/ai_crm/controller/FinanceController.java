package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @PostMapping("/transactions")
    public ResponseEntity<?> record(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "data", financeService.record(body)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        var data = (start != null && end != null) ? financeService.getByDateRange(start, end)
                : (type != null) ? financeService.getByType(type)
                : financeService.getAll();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        return ResponseEntity.ok(Map.of("success", true, "data", financeService.getSummary(start, end)));
    }
}
