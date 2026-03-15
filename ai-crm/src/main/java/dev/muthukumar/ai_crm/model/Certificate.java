package dev.muthukumar.ai_crm.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_number", unique = true, nullable = false)
    private String certificateNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id", nullable = false, unique = true)
    private Allocation allocation;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "roll_no")
    private String rollNo;

    @Column(name = "college_name")
    private String collegeName;

    private String department;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "program_title")
    private String programTitle;

    private String category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    private String grade;
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
