package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.enums.PaymentMode;
import dev.muthukumar.ai_crm.enums.TransactionType;
import dev.muthukumar.ai_crm.exception.ResourceNotFoundException;
import dev.muthukumar.ai_crm.model.FinanceTransaction;
import dev.muthukumar.ai_crm.model.OfficeProject;
import dev.muthukumar.ai_crm.repository.FinanceTransactionRepository;
import dev.muthukumar.ai_crm.repository.OfficeProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceTransactionRepository financeRepo;
    private final OfficeProjectRepository officeProjectRepo;

    public FinanceTransaction record(Map<String, Object> body) {
        FinanceTransaction tx = new FinanceTransaction();
        tx.setType(TransactionType.valueOf((String) body.get("type")));
        tx.setAmount(new BigDecimal(body.get("amount").toString()));
        tx.setCategory((String) body.get("category"));
        tx.setTransactionDate(LocalDate.parse((String) body.get("transactionDate")));
        if (body.get("description")  != null) tx.setDescription((String) body.get("description"));
        if (body.get("paymentMode")  != null) tx.setPaymentMode(PaymentMode.valueOf((String) body.get("paymentMode")));
        if (body.get("referenceNo")  != null) tx.setReferenceNo((String) body.get("referenceNo"));
        if (body.get("notes")        != null) tx.setNotes((String) body.get("notes"));
        if (body.get("officeProjectId") != null) {
            Long pid = Long.parseLong(body.get("officeProjectId").toString());
            officeProjectRepo.findById(pid).ifPresent(tx::setOfficeProject);
        }
        return financeRepo.save(tx);
    }

    public List<FinanceTransaction> getAll()              { return financeRepo.findAll(); }
    public List<FinanceTransaction> getByType(String t)   { return financeRepo.findByType(TransactionType.valueOf(t)); }

    public List<FinanceTransaction> getByDateRange(String start, String end) {
        return financeRepo.findByTransactionDateBetweenOrderByTransactionDateDesc(
                LocalDate.parse(start), LocalDate.parse(end));
    }

    public Map<String, Object> getSummary(String start, String end) {
        LocalDate s = start != null ? LocalDate.parse(start) : LocalDate.now().withDayOfMonth(1);
        LocalDate e = end   != null ? LocalDate.parse(end)   : LocalDate.now();

        BigDecimal totalIncome  = financeRepo.sumByTypeAndDateRange(TransactionType.INCOME,  s, e);
        BigDecimal totalExpense = financeRepo.sumByTypeAndDateRange(TransactionType.EXPENSE, s, e);

        Map<String, BigDecimal> incomeByCategory  = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();
        financeRepo.sumByCategoryAndTypeAndDateRange(TransactionType.INCOME,  s, e).forEach(row -> incomeByCategory.put((String) row[0],  (BigDecimal) row[1]));
        financeRepo.sumByCategoryAndTypeAndDateRange(TransactionType.EXPENSE, s, e).forEach(row -> expenseByCategory.put((String) row[0], (BigDecimal) row[1]));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalIncome",       totalIncome);
        result.put("totalExpense",      totalExpense);
        result.put("netBalance",        totalIncome.subtract(totalExpense));
        result.put("incomeByCategory",  incomeByCategory);
        result.put("expenseByCategory", expenseByCategory);
        result.put("periodStart",       s.toString());
        result.put("periodEnd",         e.toString());
        return result;
    }
}
