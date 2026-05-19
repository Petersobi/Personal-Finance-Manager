package com.peter.financeapp.repository;

import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.service.report.dto.OverallSummaryDTO;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionRepository {
    void save(Transaction transaction);
    void delete(Long id);
    void softDelete(Long id);
    void update(Long id,Long categoryId, BigDecimal amount, String description, LocalDate date);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndMonth(Long userId,String month);
    List<TransactionReportDTO> findReportData(Long userId,String month);
    List<TransactionReportDTO> findReportData(Long userId);
    Map<Long, BigDecimal> getMonthlyCategorySpending(Long userId,String month);
    List<TransactionReportDTO> findRecentTransactions(Long userId,int limit);
    List<TransactionReportDTO> findTransactions(Long userId);
    List<TransactionReportDTO> findTransactionsForMonth(Long userId,String month);

}
