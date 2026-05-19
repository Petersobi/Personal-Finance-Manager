package com.peter.financeapp.dao;

import com.peter.financeapp.model.Budget;
import com.peter.financeapp.repository.BudgetRepository;
import com.peter.financeapp.util.DButil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SQLiteBudgetDAO implements BudgetRepository {
    public void save(Budget budget){
        String sql = """
                INSERT INTO budgets(
                user_id,category_id,month,amount)
                VALUES (?,?,?,?);
                """;
        try(Connection connection = DButil.getConnection();
            PreparedStatement preparedStatement= connection.prepareStatement(sql)) {
            preparedStatement.setLong(1,budget.getUserId());
            preparedStatement.setLong(2,budget.getCategoryId());
            preparedStatement.setString(3,budget.getMonth());
            preparedStatement.setString(4,budget.getAmount().toString());

            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if(keys.next()){
                budget.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error saving budget " + e.getMessage(),e);
        }
    }
    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM budgets
                WHERE id = ?
                """;
        try(Connection conn = DButil.getConnection();
            PreparedStatement prs = conn.prepareStatement(sql)) {
            prs.setLong(1,id);
            prs.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting budget",e);
        }
    }

    @Override
    public List<Budget> findUserBudgets(Long userId, String month) {
        String sql = """
                SELECT * FROM budgets WHERE
                user_id = ?
                AND month LIKE ?
                """;
        List<Budget> budgets = new ArrayList<>();
        try (Connection conn = DButil.getConnection();
             PreparedStatement prs = conn.prepareStatement(sql)) {
            prs.setLong(1, userId);
            prs.setString(2, month + "%");

            ResultSet rs = prs.executeQuery();
            while (rs.next()){
                budgets.add(maprow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching Budgets",e);
        } return budgets;
    }
    @Override
    public Budget findUserBudget(Long id) {
        String sql = """
                SELECT * FROM budgets WHERE
                id = ?
                """;

        try (Connection conn = DButil.getConnection();
             PreparedStatement prs = conn.prepareStatement(sql)) {
            prs.setLong(1,id);

            ResultSet rs = prs.executeQuery();
            if (rs.next()){
              return new Budget( rs.getLong("id"),
                      rs.getLong("user_id"),
                      rs.getLong("category_id"),
                      rs.getString("month"),
                      new BigDecimal(rs.getString("amount"))
              );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching Budget",e);
        } return null;
    }

    @Override
    public Budget findByUserCategoryAndMonth(Long userId, Long categoryId, String month) {
        String sql = """
                SELECT * FROM budgets WHERE
                user_id = ? AND category_id = ?
                AND month = ?
                """;
        try (Connection conn = DButil.getConnection();
        PreparedStatement prs = conn.prepareStatement(sql)){
            prs.setLong(1,userId);
            prs.setLong(2,categoryId);
            prs.setString(3,month);

            ResultSet rs = prs.executeQuery();
            if (rs.next()){
                return maprow(rs);
            } return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error fetching budget",e);
        }

    }

    @Override
    public void update(Budget budget) {
        String sql = """
                UPDATE budgets SET
                amount = ? WHERE user_id = ?
                AND category_id = ? AND month = ?
                """;
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setString(1,budget.getAmount().toString());
            prs.setLong(2,budget.getUserId());
            prs.setLong(3,budget.getCategoryId());
            prs.setString(4,budget.getMonth());

            prs.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error updating budget",e);
        }

    }

    private Budget maprow(ResultSet rs) throws SQLException{
        return new Budget(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("category_id"),
                rs.getString("month"),
                new BigDecimal(rs.getString("amount"))
        );
    }
}
