package com.peter.financeapp.service.report.dto;

import java.math.BigDecimal;

public class CategoryReportDTO {
    private final String categoryName;
    private final BigDecimal totalAmount;

    public CategoryReportDTO(String categoryName,BigDecimal totalAmount){
        this.categoryName = categoryName; this.totalAmount = totalAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
