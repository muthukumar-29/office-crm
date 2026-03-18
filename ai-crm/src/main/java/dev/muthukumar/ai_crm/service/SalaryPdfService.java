package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Salary;
import dev.muthukumar.ai_crm.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Generates a professional payslip as HTML (rendered PDF via browser / iText if added).
 * Returns HTML string that can be served to the frontend for print-to-PDF.
 * To generate PDF bytes server-side, add Flying Saucer (iText) dependency.
 */
@Service
@RequiredArgsConstructor
public class SalaryPdfService {

    public String generatePayslipHtml(Salary s) {
        User emp = s.getEmployee();
        String empName    = emp != null ? emp.getName()  : "-";
        String empEmail   = emp != null ? emp.getEmail() : "-";
        String empRole    = emp != null ? emp.getRole().name() : "-";
        String empPhone   = emp != null ? emp.getPhone() : "-";

        String payDate = s.getPayDate() != null
                ? s.getPayDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-";

        return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<style>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: 'Inter', sans-serif; background: #f8fafc; color: #1e293b; }
  .page { max-width: 800px; margin: 0 auto; background: #fff; }
  /* Header */
  .header { background: linear-gradient(135deg, #0f172a 0%%, #1e3a5f 100%%); color: #fff; padding: 32px 40px; }
  .header-top { display: flex; justify-content: space-between; align-items: flex-start; }
  .company-name { font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }
  .company-sub  { font-size: 12px; color: #94a3b8; margin-top: 4px; }
  .payslip-title { text-align: right; }
  .payslip-title h2 { font-size: 20px; font-weight: 600; color: #38bdf8; }
  .payslip-title p  { font-size: 13px; color: #94a3b8; margin-top: 4px; }
  .header-divider { border-top: 1px solid rgba(255,255,255,0.1); margin: 24px 0; }
  .header-meta { display: flex; gap: 40px; }
  .meta-item label { font-size: 11px; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.5px; }
  .meta-item p     { font-size: 14px; font-weight: 500; color: #e2e8f0; margin-top: 2px; }

  /* Employee info */
  .section { padding: 28px 40px; }
  .section-title { font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.8px; color: #64748b; margin-bottom: 16px; }
  .emp-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; background: #f8fafc; border-radius: 10px; padding: 20px; border: 1px solid #e2e8f0; }
  .emp-field label { font-size: 11px; color: #64748b; }
  .emp-field p     { font-size: 14px; font-weight: 500; color: #1e293b; margin-top: 3px; }

  /* Earnings / Deductions table */
  .salary-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; padding: 0 40px 28px; }
  .sal-card { border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; }
  .sal-card-header { padding: 12px 18px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.6px; }
  .earn-header { background: #dcfce7; color: #15803d; }
  .dedu-header { background: #fee2e2; color: #b91c1c; }
  .sal-row { display: flex; justify-content: space-between; padding: 10px 18px; font-size: 13px; border-top: 1px solid #f1f5f9; }
  .sal-row:nth-child(even) { background: #fafafa; }
  .sal-row .label { color: #475569; }
  .sal-row .amount { font-weight: 500; color: #1e293b; font-variant-numeric: tabular-nums; }

  /* Net pay summary */
  .net-section { margin: 0 40px 28px; background: linear-gradient(135deg, #0f172a, #1e3a5f); border-radius: 12px; padding: 24px 28px; color: #fff; display: flex; justify-content: space-between; align-items: center; }
  .net-label { font-size: 13px; color: #94a3b8; }
  .net-amount { font-size: 32px; font-weight: 700; color: #38bdf8; letter-spacing: -1px; }
  .net-breakdown { font-size: 12px; color: #64748b; margin-top: 4px; }

  /* Status badge */
  .status-paid    { background: #dcfce7; color: #15803d; padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 600; }
  .status-pending { background: #fef3c7; color: #92400e; padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 600; }

  /* Footer */
  .footer { border-top: 1px solid #e2e8f0; padding: 20px 40px; display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #64748b; }
  .footer .note { font-style: italic; }
  @media print {
    body { background: #fff; }
    .page { box-shadow: none; }
  }
</style>
</head>
<body>
<div class="page">
  <!-- Header -->
  <div class="header">
    <div class="header-top">
      <div>
        <div class="company-name">AI CRM Solutions</div>
        <div class="company-sub">Employee Payslip</div>
      </div>
      <div class="payslip-title">
        <h2>PAYSLIP</h2>
        <p>%s</p>
        <p style="margin-top:8px"><span class="%s">%s</span></p>
      </div>
    </div>
    <div class="header-divider"></div>
    <div class="header-meta">
      <div class="meta-item"><label>Pay Period</label><p>%s</p></div>
      <div class="meta-item"><label>Pay Date</label><p>%s</p></div>
      <div class="meta-item"><label>Payslip ID</label><p>#SAL-%05d</p></div>
    </div>
  </div>

  <!-- Employee Info -->
  <div class="section">
    <div class="section-title">Employee Details</div>
    <div class="emp-grid">
      <div class="emp-field"><label>Full Name</label><p>%s</p></div>
      <div class="emp-field"><label>Email</label><p>%s</p></div>
      <div class="emp-field"><label>Phone</label><p>%s</p></div>
      <div class="emp-field"><label>Designation</label><p>%s</p></div>
      <div class="emp-field"><label>Payment Mode</label><p>%s</p></div>
      <div class="emp-field"><label>Transaction Ref</label><p>%s</p></div>
    </div>
  </div>

  <!-- Earnings & Deductions -->
  <div class="salary-grid">
    <div class="sal-card">
      <div class="sal-card-header earn-header">Earnings</div>
      %s
    </div>
    <div class="sal-card">
      <div class="sal-card-header dedu-header">Deductions</div>
      %s
    </div>
  </div>

  <!-- Net Pay -->
  <div class="net-section">
    <div>
      <div class="net-label">Gross Salary</div>
      <div style="font-size:16px;font-weight:600;color:#e2e8f0;margin-top:4px">₹ %s</div>
      <div class="net-breakdown">Total Deductions: ₹ %s</div>
    </div>
    <div style="text-align:right">
      <div class="net-label">Net Pay (Take Home)</div>
      <div class="net-amount">₹ %s</div>
    </div>
  </div>

  <!-- Notes -->
  %s

  <!-- Footer -->
  <div class="footer">
    <div class="note">This is a computer-generated payslip. No signature required.</div>
    <div>Generated on %s</div>
  </div>
</div>
</body>
</html>
"""
                .formatted(
                        // Header
                        s.getPayMonth(),
                        "PAID".equals(s.getStatus().name()) ? "status-paid" : "status-pending",
                        s.getStatus().name(),
                        s.getPayMonth(), payDate, s.getId(),
                        // Employee
                        empName, empEmail, empPhone,
                        empRole,
                        s.getPaymentMode() != null ? s.getPaymentMode() : "-",
                        s.getTransactionRef() != null ? s.getTransactionRef() : "-",
                        // Earnings rows
                        buildRow("Basic Salary", s.getBasicSalary())
                                + buildRow("HRA", s.getHra())
                                + buildRow("Transport Allowance", s.getTransportAllowance())
                                + buildRow("Other Allowance", s.getOtherAllowance())
                                + buildRow("Bonus", s.getBonus()),
                        // Deduction rows
                        buildRow("PF Deduction", s.getPfDeduction())
                                + buildRow("Tax (TDS)", s.getTaxDeduction())
                                + buildRow("Other Deduction", s.getOtherDeduction()),
                        // Totals
                        fmt(s.getGrossSalary()),
                        fmt(s.getPfDeduction().add(s.getTaxDeduction()).add(s.getOtherDeduction())),
                        fmt(s.getNetSalary()),
                        // Notes
                        s.getNotes() != null && !s.getNotes().isBlank()
                                ? "<div class=\"section\"><div class=\"section-title\">Notes</div><p style=\"font-size:13px;color:#475569\">"+s.getNotes()+"</p></div>"
                                : "",
                        java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                );
    }

    private String buildRow(String label, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return "";
        return "<div class=\"sal-row\"><span class=\"label\">" + label
                + "</span><span class=\"amount\">₹ " + fmt(amount) + "</span></div>";
    }

    private String fmt(BigDecimal v) {
        if (v == null) return "0.00";
        return String.format("%,.2f", v);
    }
}
