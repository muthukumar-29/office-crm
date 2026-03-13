package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.Category;
import dev.muthukumar.ai_crm.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category Not Found"));
    }

    public Page<Category> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable);
    }

    public Category create(Category category){
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category updated){
        Category existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Long id){
        categoryRepository.deleteById(id);
    }

}
