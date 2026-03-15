package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> getByStudentId(String studentId);

    @Query("SELECT s FROM Student s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
            "LOWER(s.rollNo) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Student> searchStudents(@Param("q") String q);

}
