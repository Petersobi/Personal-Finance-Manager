package com.peter.financeapp.model;

import java.math.BigDecimal;

public class Budget {
    private Long id;
    private Long userId;
    private  Long categoryId;
    private String month;
    private BigDecimal amount;

    public Budget(Long id,Long userId,Long categoryId, String month,BigDecimal amount){
        this.id = id;this.userId = userId; this.categoryId = categoryId;this.month = month;this.amount = amount;
    }
    public Budget(Long userId,Long categoryId, String month,BigDecimal amount){
        this.userId = userId; this.categoryId = categoryId;this.month = month;this.amount = amount;
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

    public String getMonth() {
        return month;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
