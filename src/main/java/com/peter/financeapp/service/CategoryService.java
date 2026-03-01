package com.peter.financeapp.service;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.session.SessionManager;

import java.util.List;

public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final SessionManager sessionManager;

  public CategoryService(CategoryRepository categoryRepository,SessionManager sessionManager){
      this.categoryRepository = categoryRepository; this.sessionManager = sessionManager;
  }

  public void createCategory(String name, CategoryType type){
      if (!sessionManager.isloggedIn()){
          throw new IllegalStateException("User must be logged im");
      }
      if (name ==null|| name.isBlank()){
          throw new IllegalStateException("Category name cannot be empty");
      }
      Long userId = sessionManager.getCurrentUser().getId();
      Category category = new Category(userId,name,type);
      categoryRepository.save(category);
  }
  public List<Category> getUserCategories(){
      if (!sessionManager.isloggedIn()){
          throw new IllegalStateException("User must be logged im");
      }
      Long userId = sessionManager.getCurrentUser().getId();
      return categoryRepository.findByUserID(userId);
  }
  public void delectCategory(Long categoryId) {
      if (!sessionManager.isloggedIn()) {
          throw new IllegalStateException("User must be logged im");
      }
      categoryRepository.softDelete(categoryId);
  }
}
