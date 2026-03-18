package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Invoice;
import dev.muthukumar.ai_crm.service.InvoiceService;
import dev.muthukumar.ai_crm.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfGeneratorUtil pdfGenerator;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Invoice inv = invoiceService.create(body);
            return ResponseEntity.ok(Map.of("success", true, "message", "Invoice created", "data", toDto(inv)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> list = invoiceService.getAll().stream().map(this::toDto).toList();
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "data", toDto(invoiceService.getById(id))));
    }

    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<?> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(Map.of("success", true, "data", toDto(invoiceService.getByAllocation(allocationId))));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Invoice inv = invoiceService.getById(id);
        try {
            byte[] pdf = pdfGenerator.generateInvoicePdf(inv);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"invoice_" + inv.getInvoiceNumber() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }

    // ── Safe DTO — avoids LazyInitializationException on serialization ────────
    // Uses LinkedHashMap instead of Map.of() because Map.of() only supports 10 args max
    private Map<String, Object> toDto(Invoice inv) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id",            inv.getId());
        dto.put("invoiceNumber", inv.getInvoiceNumber()  != null ? inv.getInvoiceNumber()  : "");
        dto.put("clientName",    inv.getClientName()     != null ? inv.getClientName()     : "");
        dto.put("clientEmail",   inv.getClientEmail()    != null ? inv.getClientEmail()    : "");
        dto.put("clientPhone",   inv.getClientPhone()    != null ? inv.getClientPhone()    : "");
        dto.put("clientAddress", inv.getClientAddress()  != null ? inv.getClientAddress()  : "");
        dto.put("invoiceDate",   inv.getInvoiceDate()    != null ? inv.getInvoiceDate().toString()  : "");
        dto.put("dueDate",       inv.getDueDate()        != null ? inv.getDueDate().toString()      : "");
        dto.put("subtotal",      inv.getSubtotal()       != null ? inv.getSubtotal()       : 0);
        dto.put("discount",      inv.getDiscount()       != null ? inv.getDiscount()       : 0);
        dto.put("taxPercent",    inv.getTaxPercent()     != null ? inv.getTaxPercent()     : 0);
        dto.put("taxAmount",     inv.getTaxAmount()      != null ? inv.getTaxAmount()      : 0);
        dto.put("totalAmount",   inv.getTotalAmount()    != null ? inv.getTotalAmount()    : 0);
        dto.put("amountPaid",    inv.getAmountPaid()     != null ? inv.getAmountPaid()     : 0);
        dto.put("balanceDue",    inv.getBalanceDue()     != null ? inv.getBalanceDue()     : 0);
        dto.put("status",        inv.getStatus()         != null ? inv.getStatus()         : "PAID");
        dto.put("notes",         inv.getNotes()          != null ? inv.getNotes()          : "");
        return dto;
    }
}