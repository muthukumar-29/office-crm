package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> record(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment recorded", "data", paymentService.record(body)));
    }

    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<?> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(Map.of("success", true, "data", paymentService.getByAllocation(allocationId)));
    }

    @GetMapping
    public ResponseEntity<?> getByDateRange(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(Map.of("success", true, "data", paymentService.getByDateRange(start, end)));
    }
}
