package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.model.Category;
import org.example.ecommerce.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Categories API")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        var savedCategory = categoryService.create(category);
        return ResponseEntity.created(null).body(savedCategory);
    }

    @PostMapping(params = "multiple")
    public ResponseEntity<List<Category>> create(
            @RequestBody List<Category> categories,
            @RequestParam(name = "multiple") String ignored
    ) {
        var savedCategories = categoryService.create(categories);
        return ResponseEntity.created(null).body(savedCategories);
    }

    @GetMapping
    public ResponseEntity<List<Category>> readAll() {
        var categories = categoryService.readAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Category> read(@PathVariable String name) {
        var category = categoryService.read(name);
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{name}")
    public ResponseEntity<Category> update(
            @PathVariable String name,
            @RequestBody Category newCategory
    ) {
        var updatedCategory = categoryService.update(name, newCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        categoryService.delete(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Category> deleteAll() {
        categoryService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
