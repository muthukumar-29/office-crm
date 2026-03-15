package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.enums.AllocationCategory;
import dev.muthukumar.ai_crm.enums.AllocationStatus;
import dev.muthukumar.ai_crm.enums.PaymentStatus;
import dev.muthukumar.ai_crm.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// REPLACE your existing AllocationRepository with this
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    List<Allocation> findByStudentId(Long studentId);

    List<Allocation> findByCategory(AllocationCategory category);

    List<Allocation> findByCategoryAndAllocationStatus(AllocationCategory category, AllocationStatus status);

    List<Allocation> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT a FROM Allocation a WHERE a.student.id = :sid AND a.allocationStatus = 'ACTIVE'")
    List<Allocation> findActiveByStudent(@Param("sid") Long studentId);

    @Query("SELECT a FROM Allocation a WHERE a.paymentStatus = 'PAID' AND a.invoiceGenerated = false")
    List<Allocation> findPaidWithoutInvoice();

    @Query("SELECT a FROM Allocation a WHERE a.allocationStatus = 'COMPLETED' AND a.certificateIssued = false")
    List<Allocation> findCompletedWithoutCertificate();
}
