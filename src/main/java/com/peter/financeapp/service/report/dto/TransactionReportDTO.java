package com.peter.financeapp.service.report.dto;

import com.peter.financeapp.model.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionReportDTO {
    private final BigDecimal amount;
    private final String categoryName;
    private final CategoryType  categoryType;
    private final LocalDate localDate;

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
