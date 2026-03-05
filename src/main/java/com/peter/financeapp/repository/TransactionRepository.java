package com.peter.financeapp.repository;

import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;

import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndMonth(Long userId,String month);
    List<TransactionReportDTO> findReportData(Long userId,String month);
}
