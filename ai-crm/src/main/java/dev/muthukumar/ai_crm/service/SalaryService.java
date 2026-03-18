package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.dto.SalaryRequest;
import dev.muthukumar.ai_crm.enums.SalaryStatus;
import dev.muthukumar.ai_crm.enums.TransactionType;
import dev.muthukumar.ai_crm.model.FinanceTransaction;
import dev.muthukumar.ai_crm.model.Salary;
import dev.muthukumar.ai_crm.repository.FinanceTransactionRepository;
import dev.muthukumar.ai_crm.repository.SalaryRepository;
import dev.muthukumar.ai_crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository           salaryRepo;
    private final UserRepository             userRepo;
    private final FinanceTransactionRepository financeRepo;

    public List<Salary> getAll() {
        return salaryRepo.findAllWithEmployee();
    }

    public List<Salary> getByEmployee(Long employeeId) {
        return salaryRepo.findByEmployeeIdOrderByPayMonthDesc(employeeId);
    }

    public Salary create(SalaryRequest req) {
        if (salaryRepo.existsByEmployeeIdAndPayMonth(req.employeeId, req.payMonth)) {
            throw new RuntimeException("Salary already generated for this employee for " + req.payMonth);
        }

        Salary s = new Salary();
        s.setEmployee(userRepo.findById(req.employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        s.setPayMonth(req.payMonth);
        s.setPayDate(req.payDate != null ? LocalDate.parse(req.payDate) : null);
        s.setBasicSalary(req.basicSalary);
        s.setHra(orZero(req.hra));
        s.setTransportAllowance(orZero(req.transportAllowance));
        s.setOtherAllowance(orZero(req.otherAllowance));
        s.setBonus(orZero(req.bonus));
        s.setPfDeduction(orZero(req.pfDeduction));
        s.setTaxDeduction(orZero(req.taxDeduction));
        s.setOtherDeduction(orZero(req.otherDeduction));
        s.setPaymentMode(req.paymentMode);
        s.setTransactionRef(req.transactionRef);
        s.setNotes(req.notes);

        // Compute gross and net
        BigDecimal gross = s.getBasicSalary()
                .add(s.getHra())
                .add(s.getTransportAllowance())
                .add(s.getOtherAllowance())
                .add(s.getBonus());
        BigDecimal deductions = s.getPfDeduction()
                .add(s.getTaxDeduction())
                .add(s.getOtherDeduction());
        s.setGrossSalary(gross);
        s.setNetSalary(gross.subtract(deductions));
        s.setStatus(SalaryStatus.PENDING);

        return salaryRepo.save(s);
    }

    public Salary markPaid(Long id) {
        Salary s = salaryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));
        s.setStatus(SalaryStatus.PAID);
        s.setPayDate(LocalDate.now());
        salaryRepo.save(s);

        // Auto-create finance transaction for the expense
        FinanceTransaction tx = new FinanceTransaction();
        tx.setType(TransactionType.EXPENSE);
        tx.setAmount(s.getNetSalary());
        tx.setDescription("Salary paid to " + s.getEmployee().getName()
                + " for " + s.getPayMonth());
        tx.setReferenceId(s.getId());
        tx.setReferenceType("SALARY");
        financeRepo.save(tx);

        return s;
    }

    public Salary getById(Long id) {
        return salaryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary not found"));
    }

    private BigDecimal orZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
