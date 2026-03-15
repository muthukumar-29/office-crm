package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByAllocationId(Long allocationId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.invoiceNumber, 5) AS int)), 0) FROM Invoice i WHERE i.invoiceNumber LIKE 'INV-%'")
    Integer findMaxSequence();
}
