package com.peter.financeapp.repository;

import com.peter.financeapp.model.Budget;

public interface BudgetRepository {
    void save(Budget budget);
    Budget findByUserCategoryAndMonth(Long userId,Long categoryId,String month);
    void update(Budget budget);
}
