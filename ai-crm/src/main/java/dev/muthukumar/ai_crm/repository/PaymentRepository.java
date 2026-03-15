package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAllocationIdOrderByPaymentDateDesc(Long allocationId);
    Optional<Payment> findByReceiptNumber(String receiptNumber);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end ORDER BY p.paymentDate DESC")
    List<Payment> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.allocation.id = :id")
    BigDecimal sumByAllocationId(@Param("id") Long allocationId);
}
