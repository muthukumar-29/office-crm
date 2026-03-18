package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Salary;
import dev.muthukumar.ai_crm.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SalaryPdfService {

    private static final String COMPANY_NAME  = "Anjana Infotech";
    private static final String COMPANY_TAG   = "ISO 9001:2015 Certified | Software Development &amp; Training";
    private static final String COMPANY_ADDR  = "372, Mudangiyar Road, Opp. AKDR Market, Rajapalayam";
    private static final String COMPANY_PHONE = "+91 97879 70633";
    private static final String COMPANY_EMAIL = "info@anjanainfotech.in";
    private static final String COMPANY_WEB   = "www.anjanainfotech.in";

    public String generatePayslipHtml(Salary s) {
        User emp = s.getEmployee();
        String empName   = emp != null ? emp.getName()  : "-";
        String empEmail  = emp != null ? emp.getEmail() : "-";
        String empRole   = emp != null ? emp.getRole().name() : "-";
        String empPhone  = emp != null && emp.getPhone() != null ? emp.getPhone() : "-";
        String payDate   = s.getPayDate() != null
                ? s.getPayDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "-";

        // earnings & deductions rows
        String earningsRows =
                buildRow("Basic Salary",        s.getBasicSalary())
                        + buildRow("HRA",               s.getHra())
                        + buildRow("Transport Allowance", s.getTransportAllowance())
                        + buildRow("Other Allowance",   s.getOtherAllowance())
                        + buildRow("Bonus",             s.getBonus());

        String deductionRows =
                buildRow("PF Deduction",        s.getPfDeduction())
                        + buildRow("Tax (TDS)",         s.getTaxDeduction())
                        + buildRow("Other Deduction",   s.getOtherDeduction());

        BigDecimal totalDeductions = orZero(s.getPfDeduction())
                .add(orZero(s.getTaxDeduction()))
                .add(orZero(s.getOtherDeduction()));

        String statusClass = "PAID".equals(s.getStatus().name()) ? "status-paid" : "status-pending";
        String notesHtml = (s.getNotes() != null && !s.getNotes().isBlank())
                ? "<div class='section'><div class='section-title'>Notes</div>"
                + "<p style='font-size:13px;color:#475569'>" + s.getNotes() + "</p></div>"
                : "";

        String generatedOn = java.time.LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        return "<!DOCTYPE html>\n"
                + "<html lang='en'><head><meta charset='UTF-8'/>\n"
                + "<style>\n"
                + "  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');\n"
                + "  *{box-sizing:border-box;margin:0;padding:0}\n"
                + "  body{font-family:'Inter',sans-serif;background:#f1f5f9;color:#1e293b}\n"
                + "  .page{max-width:800px;margin:0 auto;background:#fff}\n"
                + "  /* ─── Header ─── */\n"
                + "  .header{background:linear-gradient(135deg,#0a50b4 0%,#1a3a6f 100%);color:#fff;padding:28px 40px}\n"
                + "  .header-top{display:flex;justify-content:space-between;align-items:flex-start}\n"
                + "  .company-block .company-name{font-size:22px;font-weight:700;letter-spacing:-0.3px}\n"
                + "  .company-block .company-tag{font-size:11px;color:#bfdbfe;margin-top:3px}\n"
                + "  .company-block .company-contact{font-size:10px;color:#93c5fd;margin-top:2px}\n"
                + "  .payslip-block{text-align:right}\n"
                + "  .payslip-block h2{font-size:20px;font-weight:700;color:#fed7aa}\n"
                + "  .payslip-block .sub{font-size:12px;color:#bfdbfe;margin-top:3px}\n"
                + "  .header-divider{border-top:1px solid rgba(255,255,255,0.15);margin:18px 0}\n"
                + "  .header-meta{display:flex;gap:40px}\n"
                + "  .meta-item label{font-size:10px;color:#93c5fd;text-transform:uppercase;letter-spacing:0.5px}\n"
                + "  .meta-item p{font-size:14px;font-weight:600;color:#e2e8f0;margin-top:2px}\n"
                + "  /* ─── Status badge ─── */\n"
                + "  .status-paid{background:#dcfce7;color:#15803d;padding:3px 10px;border-radius:99px;font-size:11px;font-weight:600}\n"
                + "  .status-pending{background:#fef3c7;color:#92400e;padding:3px 10px;border-radius:99px;font-size:11px;font-weight:600}\n"
                + "  /* ─── Section ─── */\n"
                + "  .section{padding:22px 40px}\n"
                + "  .section-title{font-size:10px;font-weight:700;text-transform:uppercase;letter-spacing:0.8px;color:#64748b;margin-bottom:14px}\n"
                + "  .emp-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:14px;background:#f8fafc;border-radius:10px;padding:18px;border:1px solid #e2e8f0}\n"
                + "  .emp-field label{font-size:10px;color:#64748b}\n"
                + "  .emp-field p{font-size:13px;font-weight:500;color:#1e293b;margin-top:2px}\n"
                + "  /* ─── Salary grid ─── */\n"
                + "  .salary-grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;padding:0 40px 22px}\n"
                + "  .sal-card{border:1px solid #e2e8f0;border-radius:10px;overflow:hidden}\n"
                + "  .sal-card-header{padding:10px 16px;font-size:11px;font-weight:700;text-transform:uppercase;letter-spacing:0.6px}\n"
                + "  .earn-header{background:#dcfce7;color:#15803d}\n"
                + "  .dedu-header{background:#fee2e2;color:#b91c1c}\n"
                + "  .sal-row{display:flex;justify-content:space-between;padding:9px 16px;font-size:12px;border-top:1px solid #f1f5f9}\n"
                + "  .sal-row:nth-child(even){background:#fafafa}\n"
                + "  .sal-row .label{color:#475569}\n"
                + "  .sal-row .amount{font-weight:500;color:#1e293b}\n"
                + "  /* ─── Net pay ─── */\n"
                + "  .net-section{margin:0 40px 22px;background:linear-gradient(135deg,#0a50b4,#1a3a6f);border-radius:12px;padding:22px 28px;color:#fff;display:flex;justify-content:space-between;align-items:center}\n"
                + "  .net-label{font-size:12px;color:#93c5fd}\n"
                + "  .net-amount{font-size:30px;font-weight:800;color:#fed7aa;letter-spacing:-1px}\n"
                + "  .net-breakdown{font-size:11px;color:#64748b;margin-top:4px}\n"
                + "  /* ─── Footer ─── */\n"
                + "  .footer{border-top:1px solid #e2e8f0;padding:16px 40px;display:flex;justify-content:space-between;align-items:center;font-size:11px;color:#64748b}\n"
                + "  .footer .note{font-style:italic}\n"
                + "  @media print{body{background:#fff}.page{box-shadow:none}}\n"
                + "</style></head><body>\n"
                + "<div class='page'>\n"

                // ── Header ──
                + "  <div class='header'>\n"
                + "    <div class='header-top'>\n"
                + "      <div class='company-block'>\n"
                + "        <div class='company-name'>" + COMPANY_NAME + "</div>\n"
                + "        <div class='company-tag'>" + COMPANY_TAG + "</div>\n"
                + "        <div class='company-contact'>" + COMPANY_ADDR + "</div>\n"
                + "        <div class='company-contact'>" + COMPANY_PHONE + " &nbsp;|&nbsp; " + COMPANY_EMAIL + " &nbsp;|&nbsp; " + COMPANY_WEB + "</div>\n"
                + "      </div>\n"
                + "      <div class='payslip-block'>\n"
                + "        <h2>PAYSLIP</h2>\n"
                + "        <div class='sub'>" + s.getPayMonth() + "</div>\n"
                + "        <div style='margin-top:8px'><span class='" + statusClass + "'>" + s.getStatus().name() + "</span></div>\n"
                + "      </div>\n"
                + "    </div>\n"
                + "    <div class='header-divider'></div>\n"
                + "    <div class='header-meta'>\n"
                + "      <div class='meta-item'><label>Pay Period</label><p>" + s.getPayMonth() + "</p></div>\n"
                + "      <div class='meta-item'><label>Pay Date</label><p>" + payDate + "</p></div>\n"
                + "      <div class='meta-item'><label>Payslip ID</label><p>#SAL-" + String.format("%05d", s.getId()) + "</p></div>\n"
                + "    </div>\n"
                + "  </div>\n"

                // ── Employee details ──
                + "  <div class='section'>\n"
                + "    <div class='section-title'>Employee Details</div>\n"
                + "    <div class='emp-grid'>\n"
                + "      <div class='emp-field'><label>Full Name</label><p>" + empName + "</p></div>\n"
                + "      <div class='emp-field'><label>Email</label><p>" + empEmail + "</p></div>\n"
                + "      <div class='emp-field'><label>Phone</label><p>" + empPhone + "</p></div>\n"
                + "      <div class='emp-field'><label>Designation</label><p>" + empRole + "</p></div>\n"
                + "      <div class='emp-field'><label>Payment Mode</label><p>" + (s.getPaymentMode() != null ? s.getPaymentMode() : "-") + "</p></div>\n"
                + "      <div class='emp-field'><label>Transaction Ref</label><p>" + (s.getTransactionRef() != null ? s.getTransactionRef() : "-") + "</p></div>\n"
                + "    </div>\n"
                + "  </div>\n"

                // ── Earnings / Deductions ──
                + "  <div class='salary-grid'>\n"
                + "    <div class='sal-card'>\n"
                + "      <div class='sal-card-header earn-header'>Earnings</div>\n"
                + earningsRows
                + "    </div>\n"
                + "    <div class='sal-card'>\n"
                + "      <div class='sal-card-header dedu-header'>Deductions</div>\n"
                + deductionRows
                + "    </div>\n"
                + "  </div>\n"

                // ── Net pay ──
                + "  <div class='net-section'>\n"
                + "    <div>\n"
                + "      <div class='net-label'>Gross Salary</div>\n"
                + "      <div style='font-size:15px;font-weight:700;color:#e2e8f0;margin-top:3px'>&#8377; " + fmt(s.getGrossSalary()) + "</div>\n"
                + "      <div class='net-breakdown'>Total Deductions: &#8377; " + fmt(totalDeductions) + "</div>\n"
                + "    </div>\n"
                + "    <div style='text-align:right'>\n"
                + "      <div class='net-label'>Net Pay (Take Home)</div>\n"
                + "      <div class='net-amount'>&#8377; " + fmt(s.getNetSalary()) + "</div>\n"
                + "    </div>\n"
                + "  </div>\n"

                + notesHtml

                // ── Footer ──
                + "  <div class='footer'>\n"
                + "    <div class='note'>Computer-generated payslip — no signature required.</div>\n"
                + "    <div>Generated on " + generatedOn + " &nbsp;|&nbsp; " + COMPANY_NAME + "</div>\n"
                + "  </div>\n"
                + "</div>\n"
                + "</body></html>";
    }

    private String buildRow(String label, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return "";
        return "<div class='sal-row'>"
                + "<span class='label'>" + label + "</span>"
                + "<span class='amount'>&#8377; " + fmt(amount) + "</span>"
                + "</div>\n";
    }

    private String fmt(BigDecimal v) {
        if (v == null) return "0.00";
        return String.format("%,.2f", v);
    }

    private BigDecimal orZero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}