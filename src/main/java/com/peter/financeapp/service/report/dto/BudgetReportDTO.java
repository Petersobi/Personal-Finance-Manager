package com.peter.financeapp.service.report.dto;

import java.math.BigDecimal;

public class BudgetReportDTO {
    private final String categoryName;
    private final BigDecimal budgetAmount;
    private final BigDecimal spentAmount;
    private final BigDecimal remainingAmount;
    private String status;
    private Long budgetId;


    public BudgetReportDTO(String categoryName,BigDecimal budgetAmount,BigDecimal spentAmount,BigDecimal remaining){
        this.categoryName = categoryName; this.budgetAmount = budgetAmount; this.spentAmount = spentAmount; this.remainingAmount = remaining;
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

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }
}
