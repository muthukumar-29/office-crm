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

    private final InvoiceRepository invoiceRepo;
    private final AllocationRepository allocationRepo;
    private final AllocationService allocationService;

    @Transactional
    public Invoice create(Map<String, Object> body) {
        Invoice inv = new Invoice();

        // Allocation-linked invoice
        if (body.get("allocationId") != null) {
            Long allocationId = Long.parseLong(body.get("allocationId").toString());
            if (invoiceRepo.findByAllocationId(allocationId).isPresent())
                throw new BadRequestException("Invoice already exists for this allocation");

            Allocation a = allocationService.findById(allocationId);
            if (!PaymentStatus.PAID.equals(a.getPaymentStatus()))
                throw new BadRequestException("Invoice only generated after full payment. Status: " + a.getPaymentStatus());

            inv.setAllocation(a);
            inv.setClientName(a.getStudent().getName());
            inv.setClientEmail(a.getStudent().getEmail());
            inv.setClientPhone(a.getStudent().getPhone());
        } else {
            // Manual invoice
            inv.setClientName((String) body.get("clientName"));
            inv.setClientEmail((String) body.get("clientEmail"));
            inv.setClientPhone((String) body.get("clientPhone"));
            inv.setClientAddress((String) body.get("clientAddress"));
        }

        inv.setInvoiceDate(LocalDate.parse((String) body.get("invoiceDate")));
        if (body.get("dueDate") != null) inv.setDueDate(LocalDate.parse((String) body.get("dueDate")));
        if (body.get("notes")   != null) inv.setNotes((String) body.get("notes"));

        // Build line items
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) body.get("items");
        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map<String, Object> iData : itemsData) {
            InvoiceItem item = new InvoiceItem();
            item.setDescription((String) iData.get("description"));
            item.setQuantity(Integer.parseInt(iData.get("quantity").toString()));
            item.setUnitPrice(new BigDecimal(iData.get("unitPrice").toString()));
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setInvoice(inv);
            items.add(item);
            subtotal = subtotal.add(item.getTotalPrice());
        }

        BigDecimal discount   = body.get("discount")   != null ? new BigDecimal(body.get("discount").toString())   : BigDecimal.ZERO;
        BigDecimal taxPercent = body.get("taxPercent")  != null ? new BigDecimal(body.get("taxPercent").toString()) : BigDecimal.ZERO;
        BigDecimal afterDisc  = subtotal.subtract(discount);
        BigDecimal taxAmount  = afterDisc.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total      = afterDisc.add(taxAmount);

        inv.setSubtotal(subtotal);
        inv.setDiscount(discount);
        inv.setTaxPercent(taxPercent);
        inv.setTaxAmount(taxAmount);
        inv.setTotalAmount(total);
        inv.setAmountPaid(total);   // fully paid invoice
        inv.setBalanceDue(BigDecimal.ZERO);
        inv.setStatus("PAID");
        inv.setItems(items);
        inv.setInvoiceNumber(generateInvoiceNumber());

        Invoice saved = invoiceRepo.save(inv);

        // Mark allocation invoice generated
        if (inv.getAllocation() != null) {
            Allocation a = inv.getAllocation();
            a.setInvoiceGenerated(true);
            allocationRepo.save(a);
        }

        return saved;
    }

    public Invoice getById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }

    public Invoice getByAllocation(Long allocationId) {
        return invoiceRepo.findByAllocationId(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for allocation: " + allocationId));
    }

    public List<Invoice> getAll() { return invoiceRepo.findAll(); }

    private String generateInvoiceNumber() {
        Integer seq = invoiceRepo.findMaxSequence();
        return "INV-" + String.format("%05d", (seq == null ? 0 : seq) + 1);
    }
}
