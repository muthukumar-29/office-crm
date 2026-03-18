package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
        SELECT DISTINCT i FROM Invoice i
        LEFT JOIN FETCH i.student
        LEFT JOIN FETCH i.items
        ORDER BY i.id DESC
    """)
    List<Invoice> findAllWithDetails();
}
