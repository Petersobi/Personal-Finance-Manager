package com.peter.financeapp.service.report.dto;

import java.math.BigDecimal;

public class BudgetReportDTO {
    private final String categoryName;
    private final BigDecimal budgetAmount;
    private final BigDecimal spentAmount;
    private final BigDecimal remaining;

    public BudgetReportDTO(String categoryName,BigDecimal budgetAmount,BigDecimal spentAmount,BigDecimal remaining){
        this.categoryName = categoryName; this.budgetAmount = budgetAmount; this.spentAmount = spentAmount; this.remaining = remaining;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }
}
