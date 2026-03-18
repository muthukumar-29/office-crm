package dev.muthukumar.ai_crm.model;

import dev.muthukumar.ai_crm.enums.SalaryStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary")
@Data
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Employee ─────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    // ── Pay period ────────────────────────────────────────────
    @Column(name = "pay_month", nullable = false)          // e.g. 2025-03
    private String payMonth;

    @Column(name = "pay_date")
    private LocalDate payDate;

    // ── Earnings ──────────────────────────────────────────────
    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "hra", precision = 12, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(name = "transport_allowance", precision = 12, scale = 2)
    private BigDecimal transportAllowance = BigDecimal.ZERO;

    @Column(name = "other_allowance", precision = 12, scale = 2)
    private BigDecimal otherAllowance = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 12, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    // ── Deductions ────────────────────────────────────────────
    @Column(name = "pf_deduction", precision = 12, scale = 2)
    private BigDecimal pfDeduction = BigDecimal.ZERO;

    @Column(name = "tax_deduction", precision = 12, scale = 2)
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "other_deduction", precision = 12, scale = 2)
    private BigDecimal otherDeduction = BigDecimal.ZERO;

    // ── Computed ──────────────────────────────────────────────
    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    // ── Status & notes ────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryStatus status = SalaryStatus.PENDING;

    @Column(name = "payment_mode")
    private String paymentMode;          // BANK_TRANSFER, CASH, CHEQUE

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
