package com.example.demo.service;

import com.example.demo.model.Category;

import com.example.demo.repository.ICategoryRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
/**
 * Service class for managing categories.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final ICategoryRepository icategoryRepository;
    /**
     * Retrieve all categories from the database.
     * @return a list of categories
     */
    public List<Category> getAllCategories() {
        return icategoryRepository.findAll();
    }
    /**
     * Retrieve a category by its id.
     * @param id the id of the category to retrieve
     * @return an Optional containing the found category or empty if not found
     */
    public Optional<Category> getCategoryById(Long id) {
        return icategoryRepository.findById(id);
    }

    /**
     * Add a new category to the database.
     * @param category the category to add
     */
    public void addCategory(Category category) {
        icategoryRepository.save(category);
    }
    /**
     * Update an existing category.
     * @param category the category with updated information
     */
    public void updateCategory(@NotNull Category category) {
        Category existingCategory = icategoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        category.getId() + " does not exist."));
        existingCategory.setNameProductCategory(category.getNameProductCategory());
        icategoryRepository.save(existingCategory);
    }
    /**
     * Delete a category by its id.
     * @param id the id of the category to delete
     */
    public void deleteCategoryById(Long id) {
        if (!icategoryRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        icategoryRepository.deleteById(id);
    }
}