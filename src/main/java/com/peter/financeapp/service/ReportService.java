package com.peter.financeapp.service;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.report.dto.CategoryReportDTO;
import com.peter.financeapp.service.report.dto.MonthlySummaryDTO;
import com.peter.financeapp.session.SessionManager;

import java.math.BigDecimal;
import java.util.*;

public class ReportService {
  private final TransactionRepository transactionRepository;
  private final CategoryRepository categoryRepository;
  private SessionManager sessionManager;

  public ReportService(TransactionRepository transactionRepository,CategoryRepository categoryRepository,SessionManager sessionManager){
      this.transactionRepository = transactionRepository; this.categoryRepository = categoryRepository; this.sessionManager = sessionManager;
  }

  public MonthlySummaryDTO getMonthlySummary(String month){
      validateUser();
      Long userId = sessionManager.getCurrentUser().getId();
      List<Transaction> transactions = transactionRepository.findByUserIdAndMonth(userId,month);
      BigDecimal totalIncome = BigDecimal.ZERO;
      BigDecimal totalExpense = BigDecimal.ZERO;

      for(Transaction transaction:transactions){
          Category category = categoryRepository.findById(transaction.getCategoryId());

          if (category.getType().equals(CategoryType.INCOME)){
             totalIncome = totalIncome.add(transaction.getAmount());
          } else totalExpense = totalExpense.add(transaction.getAmount());
      }
      BigDecimal netSavings = totalIncome.subtract(totalExpense);
      return new MonthlySummaryDTO(totalIncome,totalExpense,netSavings);
  }

  public List<CategoryReportDTO> getExpenseBreakdown(String month){
      validateUser();
      Long userId = sessionManager.getCurrentUser().getId();
      List<Transaction> transactions = transactionRepository.findByUserIdAndMonth(userId,month);
      Map<String,BigDecimal> categoryTotals = new HashMap<>();
      for (Transaction transaction:transactions){
          Category category = categoryRepository.findById(transaction.getCategoryId());

          if(category.getType().equals(CategoryType.EXPENSE)){
              categoryTotals.put(category.getName(),categoryTotals.getOrDefault(category.getName(),BigDecimal.ZERO).add(transaction.getAmount()));
          }
      }
      List<CategoryReportDTO> categoryReportDTOS = new ArrayList<>();
      for(Map.Entry<String,BigDecimal>entry:categoryTotals.entrySet()){
          categoryReportDTOS.add(new CategoryReportDTO(entry.getKey(),entry.getValue()));
      }
      return categoryReportDTOS;
  }
  private void validateUser(){
      if(!sessionManager.isloggedIn()){
          throw new IllegalStateException("User must be logged in!");
      }
  }
}
