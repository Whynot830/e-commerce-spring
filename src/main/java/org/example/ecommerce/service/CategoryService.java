package org.example.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.model.Category;
import org.example.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.ecommerce.util.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    private Category save(Category category) {
        if (isNullOrBlank(category.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        return categoryRepo.save(category);
    }

    public Category create(Category category) {
        return save(category);
    }

    @Transactional
    public List<Category> create(List<Category> categories) {
        List<Category> savedCategories = new ArrayList<>();
        Category savedCategory;
        for (var category : categories) {
            savedCategory = save(category);
            savedCategories.add(savedCategory);
        }
        return savedCategories;
    }

    public List<Category> readAll() {
        return categoryRepo.findAll();
    }

    public Optional<Category> getCategory(String name) {
        return categoryRepo.findByName(name);
    }

    public Category read(String name) {
        return getCategory(name).orElseThrow(EntityNotFoundException::new);
    }

    public Category update(String name, Category newCategory) {
        var category = getCategory(name).orElseThrow(EntityNotFoundException::new);
        if (isNullOrBlank(newCategory.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        category.setName(newCategory.getName());

        return categoryRepo.save(category);
    }

    public void delete(String name) {
        var category = getCategory(name).orElseThrow(EntityNotFoundException::new);
        categoryRepo.delete(category);
    }

    public void deleteAll() {
        categoryRepo.deleteAll();
    }
}
