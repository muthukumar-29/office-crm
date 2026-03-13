package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> getByStudentId(String studentId);

}
