package com.peter.financeapp.repository;

import com.peter.financeapp.model.Category;

import java.util.List;

public interface CategoryRepository {
    void save(Category category);
    List<Category> findByUserID(Long userId);
    Category findById(Long id);
    void softDelete(Long categoryID);
}
