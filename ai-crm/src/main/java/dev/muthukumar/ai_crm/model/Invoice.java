package dev.muthukumar.ai_crm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "invoice")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    // ── Linked allocation (optional — null for manual invoices) ──────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Allocation allocation;

    // ── Client info (copied from student or entered manually) ─────────────────
    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_address")
    private String clientAddress;

    // ── Dates ─────────────────────────────────────────────────────────────────
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // ── Line items ────────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    // ── Financials ────────────────────────────────────────────────────────────
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount", precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "balance_due", precision = 12, scale = 2)
    private BigDecimal balanceDue = BigDecimal.ZERO;

    // ── Status (plain String to keep it simple) ───────────────────────────────
    @Column(name = "status")
    private String status = "PAID";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Timestamps ────────────────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.invoiceDate == null) this.invoiceDate = LocalDate.now();
        if (this.subtotal    == null) this.subtotal    = BigDecimal.ZERO;
        if (this.discount    == null) this.discount    = BigDecimal.ZERO;
        if (this.taxPercent  == null) this.taxPercent  = BigDecimal.ZERO;
        if (this.taxAmount   == null) this.taxAmount   = BigDecimal.ZERO;
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
        if (this.amountPaid  == null) this.amountPaid  = BigDecimal.ZERO;
        if (this.balanceDue  == null) this.balanceDue  = BigDecimal.ZERO;
        if (this.status      == null) this.status      = "PAID";
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}