package com.peter.financeapp.service.report.dto;

import com.peter.financeapp.model.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionReportDTO {
    private BigDecimal amount;
    private String categoryName;
    private CategoryType  categoryType;
    private LocalDate localDate;

    public TransactionReportDTO(BigDecimal amount,String categoryName,CategoryType categoryType,LocalDate localDate){
        this.amount = amount; this.categoryName = categoryName; this.categoryType = categoryType; this.localDate = localDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}
