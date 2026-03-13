package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.Category;
import dev.muthukumar.ai_crm.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category){
        return new ResponseEntity<>(categoryService.create(category), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAll(){
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id){
        return new ResponseEntity<>(categoryService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Category>> getByPages(@RequestParam int page, @RequestParam int size){
        Page<Category> categoryPage = categoryService.getByPages(page, size);
        return ResponseEntity.ok(categoryPage);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category){
        return new ResponseEntity<>(categoryService.update(id,category), HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id){
        categoryService.delete(id);
    }

}