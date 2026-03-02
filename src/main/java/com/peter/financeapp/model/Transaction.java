package com.peter.financeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;

    public Transaction(Long userId, Long categoryId, BigDecimal amount, String description, LocalDate transactionDate, LocalDateTime createdAt){
        this.userId = userId;this.categoryId = categoryId; this.amount = amount; this.description = description; this.transactionDate = transactionDate; this.createdAt = createdAt;
    }
    public Transaction(Long id,Long userId,Long categoryId,BigDecimal amount,String description,LocalDate transactionDate , LocalDateTime createdAt){
        this.id = id; this.userId = userId;this.categoryId = categoryId; this.amount = amount; this.description = description; this.transactionDate = transactionDate;this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
