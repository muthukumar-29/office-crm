package dev.muthukumar.ai_crm.dto;

import java.math.BigDecimal;

public class SalaryRequest {
    public Long employeeId;
    public String payMonth;           // "2025-03"
    public String payDate;            // "2025-03-31"

    public BigDecimal basicSalary;
    public BigDecimal hra             = BigDecimal.ZERO;
    public BigDecimal transportAllowance = BigDecimal.ZERO;
    public BigDecimal otherAllowance  = BigDecimal.ZERO;
    public BigDecimal bonus           = BigDecimal.ZERO;

    public BigDecimal pfDeduction     = BigDecimal.ZERO;
    public BigDecimal taxDeduction    = BigDecimal.ZERO;
    public BigDecimal otherDeduction  = BigDecimal.ZERO;

    public String paymentMode;
    public String transactionRef;
    public String notes;
}
