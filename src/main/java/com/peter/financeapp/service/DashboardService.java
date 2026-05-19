package com.peter.financeapp.service;

import com.peter.financeapp.service.dashboard.dto.DashboardSummaryDTO;
import com.peter.financeapp.service.report.dto.MonthlySummaryDTO;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.session.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class DashboardService {
    private final TransactionService transactionService;
    private final ReportService reportService;
    private final SessionManager sessionManager;

    public DashboardService(TransactionService transactionService,ReportService reportService,SessionManager sessionManager){
        this.transactionService = transactionService; this.reportService = reportService; this.sessionManager = sessionManager;
    }
    public DashboardSummaryDTO getDashboardSummary(){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        Long userId = sessionManager.getCurrentUser().getId();
        BigDecimal balance = transactionService.calculateBalance();

        String month = YearMonth.now().toString();
        MonthlySummaryDTO monthlySummaryDTO = reportService.getMonthlySummary(month);

        BigDecimal income = monthlySummaryDTO.getTotalIncome();
        BigDecimal expense = monthlySummaryDTO.getTotalExpense();
        BigDecimal savings = monthlySummaryDTO.getNetSavings();

        List<TransactionReportDTO> recentTransactions = transactionService.getRecentTransactions(10);

        return new DashboardSummaryDTO(balance,income,expense,savings,recentTransactions);

    }
}
