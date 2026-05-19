package com.peter.financeapp.service;

import com.peter.financeapp.model.Budget;
import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.repository.BudgetRepository;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.alert.AlertService;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.session.SessionManager;
import com.peter.financeapp.util.validator.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final SessionManager sessionManager;
    private final AlertService alertService = new AlertService();


    public TransactionService(TransactionRepository transactionRepository,CategoryRepository categoryRepository,BudgetRepository budgetRepository,SessionManager sessionManager){
        this.transactionRepository = transactionRepository; this.categoryRepository = categoryRepository;this.budgetRepository = budgetRepository; this.sessionManager = sessionManager;
    }

    public void addTransaction(Long categoryId, BigDecimal amount, String description, LocalDate transactionDate, LocalDateTime createdAt){
        if(!sessionManager.isloggedIn()){
            throw new ValidationException("User must be logged in!");
        }
        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new ValidationException("Amount must be greater than 0");
        }
        Category category = categoryRepository.findById(categoryId);
        if (category==null){
            throw new ValidationException("Category not found");
        }
        if (!category.getUserId().equals(sessionManager.getCurrentUser().getId())){
            throw new ValidationException("Invalid category for user!");
        }
        Long userId = sessionManager.getCurrentUser().getId();

        Transaction transaction = new Transaction(userId,categoryId,amount,description,transactionDate,createdAt);
        transactionRepository.save(transaction);

        if(category.getType()==CategoryType.EXPENSE) {
            String month = transactionDate.toString().substring(0,7);
            Budget budget = budgetRepository.findByUserCategoryAndMonth(userId,categoryId,month);

            if(budget!=null){

                BigDecimal spentSoFar = transactionRepository.getMonthlyCategorySpending(userId,month).getOrDefault(categoryId,BigDecimal.ZERO);
                BigDecimal remaining = budget.getAmount().subtract(spentSoFar);

                if (remaining.compareTo(BigDecimal.ZERO)<0){

                    alertService.warnOverspending(category.getName(),month,"Overspent by "+remaining.abs());
                } else if (remaining.compareTo(budget.getAmount().multiply(new BigDecimal("0.1")))<=0) {
                    alertService.warnOverspending(category.getName(),month,"Budget Almost exhausted. Remaining: "+remaining);
                }
            }
        }
    }
    public void deleteTransaction(Long id){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        transactionRepository.softDelete(id);
    }
    public void updateTransaction(Long id,Long categoryId,BigDecimal amount,String description,LocalDate date){
        transactionRepository.update(id,categoryId,amount,description,date);
    }
    public List<Transaction> getUserTransactions(){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        Long userId = sessionManager.getCurrentUser().getId();
        return transactionRepository.findByUserId(userId);

    }

    public BigDecimal calculateBalance(){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        Long userId = sessionManager.getCurrentUser().getId();

        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction:transactions){
            Category category = categoryRepository.findById(transaction.getCategoryId());

            if(category.getType()== CategoryType.INCOME){
                balance = balance.add(transaction.getAmount());
            } else {
                balance = balance.subtract(transaction.getAmount());
            }
        } return balance;
    }
    public List<TransactionReportDTO> getRecentTransactions(int limit){
        Long userId = sessionManager.getCurrentUser().getId();
        validateUser();
        return transactionRepository.findRecentTransactions(userId, limit);
    }
    public List<TransactionReportDTO> getTransactions(){

        Long userId = sessionManager.getCurrentUser().getId();
        validateUser();
        return transactionRepository.findTransactions(userId);
    }
    public List<TransactionReportDTO> getTransactionsForMonth(String month){
        long userId = sessionManager.getCurrentUser().getId();
        validateUser();
        return  transactionRepository.findTransactionsForMonth(userId,month);
    }

    private void validateUser(){
        if(!sessionManager.isloggedIn()){
            throw new ValidationException("User must be logged in!");
        }
    }

}
