package com.peter.financeapp.service.report.dto;

import java.math.BigDecimal;

public class MonthlySummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;

    public MonthlySummaryDTO(BigDecimal totalIncome,BigDecimal totalExpense,BigDecimal netSavings){
        this.totalIncome = totalIncome;   this.totalExpense =totalExpense; this.netSavings = netSavings;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }
}
