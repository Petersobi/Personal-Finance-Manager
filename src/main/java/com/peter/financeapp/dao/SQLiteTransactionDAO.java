package com.peter.financeapp.dao;

import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.AuthException;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.util.DButil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteTransactionDAO implements TransactionRepository {
    @Override
    public void save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions (
                user_id,category_id,amount,description,transaction_date,created_at)
                values(?,?,?,?,?,?);
                """;
        try (Connection connection = DButil.getConnection();
             PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,transaction.getUserId());
            prs.setLong(2,transaction.getCategoryId());
            prs.setString(3,transaction.getAmount().toString());
            prs.setString(4,transaction.getDescription());
            prs.setString(5,transaction.getTransactionDate().toString());
            prs.setString(6,transaction.getCreatedAt().toString());

            prs.executeUpdate();

            ResultSet keys = prs.getGeneratedKeys();

            if (keys.next()){
                transaction.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving transaction",e);
        }
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        String sql = """
                SELECT * FROM transactions WHERE user_id = ?
                """;
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,userId);

            ResultSet rs = prs.executeQuery();

            while (rs.next()){
                transactions.add(maprow(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error fetching transactions",e);
        } return transactions;

    }

    @Override
    public List<Transaction> findByUserIdAndMonth(Long userId,String month) {
        String sql = """
                SELECT * FROM transactions WHERE user_id = ?
                AND transaction_date LIKE ?
                """;
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,userId);
            prs.setString(2,month + "%");

            ResultSet rs = prs.executeQuery();

            while (rs.next()){
                transactions.add(maprow(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error fetching transactions for month",e);
        } return transactions;

    }
    private Transaction maprow(ResultSet rs) throws SQLException{
        return new Transaction(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("category_id"),
                new  BigDecimal(rs.getString("amount")),
                rs.getString("description"),
                LocalDate.parse(rs.getString("transaction_date")),
                LocalDateTime.parse(rs.getString("created_at"))

        );
    }

    @Override
    public List<TransactionReportDTO> findReportData(Long userId, String month) {
        String sql = """
                SELECT
                t.amount, t.transaction_date,
                c.name AS category_name,
                c.type AS category_type
                FROM transactions t
                JOIN categories c ON t.category_id = c.id
                WHERE t.user_id = ?
                AND t.transaction_date LIKE ?
                """;
        List<TransactionReportDTO> results = new ArrayList<>();

        try(Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
         prs.setLong(1,userId);
         prs.setString(2,month);

         ResultSet rs = prs.executeQuery();
         while (rs.next()){
             results.add(new TransactionReportDTO(
                     new BigDecimal(rs.getString("amount")),
                     rs.getString("category_name"),
                     CategoryType.valueOf(rs.getString("category_type")),
                     LocalDate.parse(rs.getString("transaction_date"))
             ));
         }

        } catch (SQLException e) {
            throw new DataAccessException("Error fetching report data",e);
        } return results;

    }

    @Override
    public Map<Long, BigDecimal> getMonthlyCategorySpending(Long userId, String month) {
        String sql = """
                SELECT category_id, SUM(amount) AS total_spent
                FROM transactions WHERE user_id = ?
                AND month like ? GROUP BY category_id
                """;
        Map<Long,BigDecimal> results = new HashMap<>();
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,userId);
            prs.setString(2,month);

            ResultSet rs = prs.executeQuery();

            while (rs.next()){
                Long categoryID = rs.getLong("category_id");
                BigDecimal total = new BigDecimal(rs.getString("total_spent"));
                results.put(categoryID,total);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error calculating spending",e);
        } return results;

    }
}
