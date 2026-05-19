package com.peter.financeapp.util.validationForm;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class BudgetForm {
    private  Long categoryId;
    private LocalDate month;
    private String amount;

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public LocalDate getMonth() {
        return month;
    }

    public String getAmount() {
        return amount;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
