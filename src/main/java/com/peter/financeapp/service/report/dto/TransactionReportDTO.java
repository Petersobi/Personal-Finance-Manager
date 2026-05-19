package com.peter.financeapp.service.report.dto;

import com.peter.financeapp.model.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionReportDTO {
    private Long transactionId;
    private final BigDecimal amount;
    private final String categoryName;
    private final CategoryType  categoryType;
    private final LocalDate date;
    private Long categoryID;
    private String description;

    public TransactionReportDTO(BigDecimal amount,String categoryName,CategoryType categoryType,LocalDate localDate){
        this.amount = amount; this.categoryName = categoryName; this.categoryType = categoryType; this.date = localDate;
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

    public LocalDate getDate() {
        return date;
    }

    public void setTransactionId(Long transactionId){
        this.transactionId = transactionId;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public String getDescription() {
        return description;
    }
}
