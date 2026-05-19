package com.peter.financeapp.service.report.dto;

import com.peter.financeapp.model.CategoryType;

import java.math.BigDecimal;

public class CategoryReportDTO {
    private final String categoryName;
    private final CategoryType categoryType;
    private final BigDecimal totalAmount;

    public CategoryReportDTO(String categoryName,CategoryType categoryType,BigDecimal totalAmount){
        this.categoryName = categoryName; this.categoryType = categoryType; this.totalAmount = totalAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }
}
