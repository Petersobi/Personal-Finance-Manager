package com.peter.financeapp.repository;

import com.peter.financeapp.model.Budget;

import java.util.List;

public interface BudgetRepository {
    void save(Budget budget);
    List<Budget> findUserBudgets(Long userId, String month);
    Budget findByUserCategoryAndMonth(Long userId,Long categoryId,String month);
    void update(Budget budget);
    Budget findUserBudget(Long id);
    void delete(Long id);
}
