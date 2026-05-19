package com.peter.financeapp.service;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.report.dto.CategoryReportDTO;
import com.peter.financeapp.service.report.dto.MonthlySummaryDTO;
import com.peter.financeapp.service.report.dto.OverallSummaryDTO;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.session.SessionManager;
import com.peter.financeapp.util.validator.ValidationException;

import java.math.BigDecimal;
import java.util.*;

public class ReportService {
  private final TransactionRepository transactionRepository;
  private SessionManager sessionManager;

  public ReportService(TransactionRepository transactionRepository,SessionManager sessionManager){
      this.transactionRepository = transactionRepository;this.sessionManager = sessionManager;
  }

  public MonthlySummaryDTO getMonthlySummary(String month){
      validateUser();
      Long userId = sessionManager.getCurrentUser().getId();
      List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId,month);
      BigDecimal totalIncome = BigDecimal.ZERO;
      BigDecimal totalExpense = BigDecimal.ZERO;

      for(TransactionReportDTO transaction:transactions){

          if (transaction.getCategoryType().equals(CategoryType.INCOME)){
             totalIncome = totalIncome.add(transaction.getAmount());
          } else totalExpense = totalExpense.add(transaction.getAmount());
      }
      BigDecimal netSavings = totalIncome.subtract(totalExpense);
      return new MonthlySummaryDTO(totalIncome,totalExpense,netSavings);
  }

    public OverallSummaryDTO getOverallSummary(){
        validateUser();
        Long userId = sessionManager.getCurrentUser().getId();
        List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId);
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for(TransactionReportDTO transaction:transactions){

            if (transaction.getCategoryType().equals(CategoryType.INCOME)){
                totalIncome = totalIncome.add(transaction.getAmount());
            } else totalExpense = totalExpense.add(transaction.getAmount());
        }
        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        return new OverallSummaryDTO(totalIncome,totalExpense,netSavings);
    }

  public List<CategoryReportDTO> getExpenseBreakdown(String month){
      validateUser();
      Long userId = sessionManager.getCurrentUser().getId();
      List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId,month);
      Map<String,BigDecimal> categoryTotals = new HashMap<>();
      for (TransactionReportDTO transaction:transactions){

          if(transaction.getCategoryType().equals(CategoryType.EXPENSE)){
              categoryTotals.put(transaction.getCategoryName(),categoryTotals.getOrDefault(transaction.getCategoryName(),BigDecimal.ZERO).add(transaction.getAmount()));
          }
      }
      List<CategoryReportDTO> categoryReportDTOS = new ArrayList<>();
      for(Map.Entry<String,BigDecimal>entry:categoryTotals.entrySet()){
          categoryReportDTOS.add(new CategoryReportDTO(entry.getKey(),CategoryType.EXPENSE,entry.getValue()));
      }
      return categoryReportDTOS;
  }
    public List<CategoryReportDTO> getTotalBreakdown(){
        validateUser();
        Long userId = sessionManager.getCurrentUser().getId();
        List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId);
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
    public List<CategoryReportDTO> getTotalBreakdown(String ym){
        validateUser();
        Long userId = sessionManager.getCurrentUser().getId();
        List<TransactionReportDTO> transactions = transactionRepository.findReportData(userId,ym);
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
