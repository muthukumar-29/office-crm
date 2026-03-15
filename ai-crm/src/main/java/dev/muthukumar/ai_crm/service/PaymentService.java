package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.PaymentMode;
import dev.muthukumar.ai_crm.enums.TransactionType;
import dev.muthukumar.ai_crm.exception.BadRequestException;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.*;
import dev.muthukumar.ai_crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final AllocationRepository allocationRepo;
    private final AllocationService allocationService;
    private final FinanceTransactionRepository financeRepo;

    @Transactional
    public Payment record(Map<String, Object> body) {
        Long allocationId = Long.parseLong(body.get("allocationId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String mode       = (String) body.get("paymentMode");
        String dateStr    = (String) body.get("paymentDate");

        Allocation a = allocationService.findById(allocationId);

        BigDecimal balanceDue = a.getBalanceDue() != null ? a.getBalanceDue() : BigDecimal.ZERO;
        if (amount.compareTo(balanceDue) > 0)
            throw new BadRequestException("Amount ₹" + amount + " exceeds balance due ₹" + balanceDue);

        Payment p = new Payment();
        p.setAllocation(a);
        p.setAmount(amount);
        p.setPaymentMode(PaymentMode.valueOf(mode));
        p.setPaymentDate(LocalDate.parse(dateStr));
        p.setReceiptNumber(generateReceipt());
        if (body.get("transactionRef") != null) p.setTransactionRef((String) body.get("transactionRef"));
        if (body.get("remarks")        != null) p.setRemarks((String) body.get("remarks"));
        paymentRepo.save(p);

        // Update allocation totals
        BigDecimal newPaid = (a.getAmountPaid() != null ? a.getAmountPaid() : BigDecimal.ZERO).add(amount);
        a.setAmountPaid(newPaid);
        allocationService.refreshPaymentTotals(a);

        // Auto-create finance income entry
        FinanceTransaction tx = new FinanceTransaction();
        tx.setType(TransactionType.INCOME);
        tx.setAmount(amount);
        tx.setCategory("Student Fee");
        tx.setPaymentMode(PaymentMode.valueOf(mode));
        tx.setTransactionDate(LocalDate.parse(dateStr));
        tx.setReferenceNo(p.getReceiptNumber());
        tx.setPayment(p);
        tx.setDescription(a.getStudent().getName() + " - " + a.getCategory());
        financeRepo.save(tx);

        return p;
    }

    public List<Payment> getByAllocation(Long allocationId) {
        return paymentRepo.findByAllocationIdOrderByPaymentDateDesc(allocationId);
    }

    public List<Payment> getByDateRange(String start, String end) {
        return paymentRepo.findByDateRange(LocalDate.parse(start), LocalDate.parse(end));
    }

    private String generateReceipt() {
        return "RCP-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now())
                + "-" + (System.currentTimeMillis() % 100000);
    }
}
