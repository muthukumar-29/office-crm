package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    List<Salary> findByEmployeeIdOrderByPayMonthDesc(Long employeeId);

    List<Salary> findAllByOrderByPayMonthDesc();

    boolean existsByEmployeeIdAndPayMonth(Long employeeId, String payMonth);

    @Query("SELECT s FROM Salary s JOIN FETCH s.employee ORDER BY s.payMonth DESC")
    List<Salary> findAllWithEmployee();
}
