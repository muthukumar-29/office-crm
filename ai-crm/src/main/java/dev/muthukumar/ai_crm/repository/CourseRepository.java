package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseId(String courseId);
    Optional<List<Course>> findByDomain_Id(Long domainId);
}
