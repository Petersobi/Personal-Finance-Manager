package com.peter.financeapp.service.dashboard.dto;

import com.peter.financeapp.service.report.dto.TransactionReportDTO;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDTO {
    private final BigDecimal balance;
    private final BigDecimal monthlyIncome;
    private final BigDecimal monthlyExpense;
    private final BigDecimal savings;
    private final List<TransactionReportDTO> recentTransactions;

    public DashboardSummaryDTO(BigDecimal balance,BigDecimal monthlyIncome,BigDecimal monthlyExpense,BigDecimal savings,List<TransactionReportDTO> recentTransactions){
        this.balance = balance; this.monthlyIncome = monthlyIncome; this.monthlyExpense = monthlyExpense; this.savings = savings; this.recentTransactions = recentTransactions;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getMonthlyExpense() {
        return monthlyExpense;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public List<TransactionReportDTO> getRecentTransactions() {
        return recentTransactions;
    }
}
