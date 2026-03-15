package dev.muthukumar.ai_crm.model;

import dev.muthukumar.ai_crm.enums.OfficeProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "office_project")
public class OfficeProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_contact")
    private String clientContact;

    @Column(name = "tech_stack")
    private String techStack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfficeProjectStatus status = OfficeProjectStatus.PLANNING;

    @Column(name = "start_date")
    private LocalDate startDate;

    private LocalDate deadline;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    @Column(name = "contract_value", precision = 12, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "amount_received", precision = 12, scale = 2)
    private BigDecimal amountReceived = BigDecimal.ZERO;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_manager_id")
    private User projectManager;

    @OneToMany(mappedBy = "officeProject", cascade = CascadeType.ALL)
    private List<OfficeProjectMember> members;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = OfficeProjectStatus.PLANNING;
        if (this.amountReceived == null) this.amountReceived = BigDecimal.ZERO;
    }
    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
