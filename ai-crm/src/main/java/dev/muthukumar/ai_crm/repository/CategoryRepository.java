package dev.muthukumar.ai_crm.repository;

import dev.muthukumar.ai_crm.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
