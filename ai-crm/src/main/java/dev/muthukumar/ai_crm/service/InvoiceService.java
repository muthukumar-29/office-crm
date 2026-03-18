package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.PaymentStatus;
import dev.muthukumar.ai_crm.exception.BadRequestException;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.*;
import dev.muthukumar.ai_crm.repository.AllocationRepository;
import dev.muthukumar.ai_crm.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository    invoiceRepo;
    private final AllocationRepository allocationRepo;
    private final AllocationService    allocationService;

    // ── Get all ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Invoice> getAll() {
        List<Invoice> all = invoiceRepo.findAll();
        for (Invoice inv : all) {
            if (inv.getAllocation() != null) {
                inv.getAllocation().getId();
                if (inv.getAllocation().getStudent() != null)
                    inv.getAllocation().getStudent().getName();
            }
            if (inv.getItems() != null) {
                inv.getItems().size();
            }
        }
        return all;
    }

    // ── Create ────────────────────────────────────────────────────────────────
    @Transactional
    public Invoice create(Map<String, Object> body) {
        Invoice inv = new Invoice();

        if (body.get("allocationId") != null) {
            Long allocationId = Long.parseLong(body.get("allocationId").toString());
            if (invoiceRepo.findByAllocationId(allocationId).isPresent())
                throw new BadRequestException("Invoice already exists for this allocation");

            Allocation a = allocationService.findById(allocationId);
            if (!PaymentStatus.PAID.equals(a.getPaymentStatus()))
                throw new BadRequestException(
                        "Invoice can only be generated after full payment. Status: " + a.getPaymentStatus());

            inv.setAllocation(a);
            inv.setClientName(a.getStudent().getName());
            inv.setClientEmail(a.getStudent().getEmail());
            inv.setClientPhone(a.getStudent().getPhone());
        } else {
            inv.setClientName(str(body, "clientName"));
            inv.setClientEmail(str(body, "clientEmail"));
            inv.setClientPhone(str(body, "clientPhone"));
            inv.setClientAddress(str(body, "clientAddress"));
        }

        String invoiceDateStr = str(body, "invoiceDate");
        inv.setInvoiceDate(invoiceDateStr != null ? LocalDate.parse(invoiceDateStr) : LocalDate.now());
        String dueDateStr = str(body, "dueDate");
        if (dueDateStr != null) inv.setDueDate(LocalDate.parse(dueDateStr));
        if (body.get("notes") != null) inv.setNotes(body.get("notes").toString());

        // ── Build line items ──────────────────────────────────────────────────
        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        Object itemsRaw = body.get("items");
        if (itemsRaw instanceof List) {
            List<?> rawList = (List<?>) itemsRaw;
            for (Object o : rawList) {
                if (o instanceof Map) {
                    // Cast to Map<String, Object> — fixes "capture#1 of ?" compile error
                    @SuppressWarnings("unchecked")
                    Map<String, Object> m = (Map<String, Object>) o;

                    InvoiceItem item = new InvoiceItem();
                    item.setDescription(m.getOrDefault("description", "").toString());
                    item.setQuantity(Integer.parseInt(m.getOrDefault("quantity", "1").toString()));
                    item.setUnitPrice(new BigDecimal(m.getOrDefault("unitPrice", "0").toString()));
                    item.setTotalPrice(
                            item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    item.setInvoice(inv);
                    items.add(item);
                    subtotal = subtotal.add(item.getTotalPrice());
                }
            }
        }

        // ── Totals ────────────────────────────────────────────────────────────
        BigDecimal discount = body.get("discount") != null
                ? new BigDecimal(body.get("discount").toString()) : BigDecimal.ZERO;
        BigDecimal taxPercent = body.get("taxPercent") != null
                ? new BigDecimal(body.get("taxPercent").toString()) : BigDecimal.ZERO;
        BigDecimal afterDisc = subtotal.subtract(discount);
        BigDecimal taxAmount = afterDisc
                .multiply(taxPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = afterDisc.add(taxAmount);

        inv.setSubtotal(subtotal);
        inv.setDiscount(discount);
        inv.setTaxPercent(taxPercent);
        inv.setTaxAmount(taxAmount);
        inv.setTotalAmount(total);
        inv.setAmountPaid(total);
        inv.setBalanceDue(BigDecimal.ZERO);
        inv.setStatus("PAID");
        inv.setItems(items);
        inv.setInvoiceNumber(generateInvoiceNumber());

        Invoice saved = invoiceRepo.save(inv);

        if (saved.getAllocation() != null) {
            saved.getAllocation().setInvoiceGenerated(true);
            allocationRepo.save(saved.getAllocation());
        }
        return saved;
    }

    // ── Queries ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Invoice getById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }

    @Transactional(readOnly = true)
    public Invoice getByAllocation(Long allocationId) {
        return invoiceRepo.findByAllocationId(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found for allocation: " + allocationId));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String generateInvoiceNumber() {
        Integer seq = invoiceRepo.findMaxSequence();
        return "INV-" + String.format("%05d", (seq == null ? 0 : seq) + 1);
    }

    private String str(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return (v != null && !v.toString().isBlank()) ? v.toString() : null;
    }
}