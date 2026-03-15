package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Invoice;
import dev.muthukumar.ai_crm.service.InvoiceService;
import dev.muthukumar.ai_crm.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfGeneratorUtil pdfGenerator;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Invoice created", "data", invoiceService.create(body)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", invoiceService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", invoiceService.getById(id)));
    }

    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<?> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(Map.of("success", true, "data", invoiceService.getByAllocation(allocationId)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Invoice inv = invoiceService.getById(id);
        try {
            byte[] pdf = pdfGenerator.generateInvoicePdf(inv);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice_" + inv.getInvoiceNumber() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }
}
