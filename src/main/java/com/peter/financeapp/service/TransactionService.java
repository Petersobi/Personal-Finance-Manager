package com.peter.financeapp.service;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.session.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SessionManager sessionManager;

    public TransactionService(TransactionRepository transactionRepository,CategoryRepository categoryRepository,SessionManager sessionManager){
        this.transactionRepository = transactionRepository; this.categoryRepository = categoryRepository; this.sessionManager = sessionManager;
    }

    public void addTransaction(Long categoryId, BigDecimal amount, String description, LocalDate transactionDate, LocalDateTime createdAt){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        if(amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        Category category = categoryRepository.findById(categoryId);
        if (category==null){
            throw new IllegalArgumentException("Category not found");
        }
        if (!category.getUserId().equals(sessionManager.getCurrentUser().getId())){
            throw new IllegalArgumentException("Invalid category for user!");
        }
        Long userId = sessionManager.getCurrentUser().getId();

        Transaction transaction = new Transaction(userId,categoryId,amount,description,transactionDate,createdAt);
        transactionRepository.save(transaction);
    }
    public List<Transaction> getUserTransaction(){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        Long userId = sessionManager.getCurrentUser().getId();
        return transactionRepository.findByUserId(userId);

    }
    public List<Transaction> getUserTransactionForMonth(String month){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        Long userId = sessionManager.getCurrentUser().getId();
        return transactionRepository.findByUserIdAndMonth(userId,month); }

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
}
