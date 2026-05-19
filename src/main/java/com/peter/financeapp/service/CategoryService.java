package com.peter.financeapp.service;

import com.peter.financeapp.dao.DataAccessException;
import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.report.dto.CategoryReportDTO;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.session.SessionManager;
import com.peter.financeapp.util.validator.ValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final TransactionRepository transactionRepository;
  private final SessionManager sessionManager;

  public CategoryService(CategoryRepository categoryRepository,TransactionRepository transactionRepository,SessionManager sessionManager){
      this.categoryRepository = categoryRepository; this.sessionManager = sessionManager; this.transactionRepository = transactionRepository;
  }

  public Category createCategory(String name, CategoryType type){
      if (!sessionManager.isloggedIn()){
          throw new ValidationException("User must be logged im");
      }
      if (name ==null|| name.isBlank()){
          throw new ValidationException("Category name cannot be empty");
      }
      if(type==null){
          throw new ValidationException("Category type cannot be empty");
      }
      Long userId = sessionManager.getCurrentUser().getId();
      Category category = new Category(userId,name,type);

      try {
          categoryRepository.save(category);
      } catch (DataAccessException e) {
          if(e.getMessage().toLowerCase().contains("unique")){
              throw new ValidationException("Category Already Exists!");
          }
      }

      return category;
  }
  public List<Category> getUserCategories(){
      if (!sessionManager.isloggedIn()){
          throw new ValidationException("User must be logged im");
      }
      Long userId = sessionManager.getCurrentUser().getId();
      return categoryRepository.findByUserID(userId);
  }
  public void delectCategory(Long categoryId) {
      if (!sessionManager.isloggedIn()) {
          throw new ValidationException("User must be logged im");
      }
      categoryRepository.softDelete(categoryId);
  }
  public Category findById(Long categoryID){
      if (!sessionManager.isloggedIn()) {
          throw new ValidationException("User must be logged im");
      }
      return categoryRepository.findById(categoryID);
  }
  public void updateCategory(Long categoryId,String categoryName,String categoryType){
      if (!sessionManager.isloggedIn()) {
          throw new ValidationException("User must be logged im");
      }
      categoryRepository.update(categoryId,categoryName,categoryType);
  }
  public List<Category> getCategoryByType(String type){
      if(!sessionManager.isloggedIn()){
          throw new ValidationException("User must be logged in!");
      }
      List<Category> categories = categoryRepository.findByUserID(sessionManager.getCurrentUser().getId());
      return categories.stream().filter(c -> c.getType().equals(CategoryType.valueOf(type))).toList();
  }
  public List<CategoryReportDTO> getCategoryBreakDown(String date){
      validateUser();
      Long userId = sessionManager.getCurrentUser().getId();
      List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId,date);
      Map<String, BigDecimal> categoryTotalsIncome = new HashMap<>();
      Map<String, BigDecimal> categoryTotalsExpense = new HashMap<>();
      for (TransactionReportDTO transaction:transactions){

          if(transaction.getCategoryType().equals(CategoryType.EXPENSE)){
              categoryTotalsExpense.put(transaction.getCategoryName(),categoryTotalsExpense.getOrDefault(transaction.getCategoryName(),BigDecimal.ZERO).add(transaction.getAmount()));
          } else {
              categoryTotalsIncome.put(transaction.getCategoryName(),categoryTotalsIncome.getOrDefault(transaction.getCategoryName(),BigDecimal.ZERO).add(transaction.getAmount()));
          }
      }
      List<CategoryReportDTO> categoryReportDTOS = new ArrayList<>();
      for(Map.Entry<String,BigDecimal>entry:categoryTotalsExpense.entrySet()){
          categoryReportDTOS.add(new CategoryReportDTO(entry.getKey(),CategoryType.EXPENSE,entry.getValue()));
      }
      for(Map.Entry<String,BigDecimal>entry:categoryTotalsIncome.entrySet()){
          categoryReportDTOS.add(new CategoryReportDTO(entry.getKey(),CategoryType.INCOME,entry.getValue()));
      }
      return categoryReportDTOS;
  }
    private void validateUser(){
        if(!sessionManager.isloggedIn()){
            throw new ValidationException("User must be logged in!");
        }
    }
}
