package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.enums.TransactionType;
import dev.muthukumar.ai_crm.model.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {
    List<FinanceTransaction> findByType(TransactionType type);

    List<FinanceTransaction> findByTransactionDateBetweenOrderByTransactionDateDesc(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinanceTransaction f WHERE f.type = :type AND f.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByTypeAndDateRange(@Param("type") TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT f.category, SUM(f.amount) FROM FinanceTransaction f WHERE f.type = :type AND f.transactionDate BETWEEN :start AND :end GROUP BY f.category ORDER BY SUM(f.amount) DESC")
    List<Object[]> sumByCategoryAndTypeAndDateRange(@Param("type") TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
