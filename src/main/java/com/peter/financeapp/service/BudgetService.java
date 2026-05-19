package com.peter.financeapp.service;

import com.peter.financeapp.dao.DataAccessException;
import com.peter.financeapp.model.Budget;
import com.peter.financeapp.model.Category;
import com.peter.financeapp.repository.BudgetRepository;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.report.dto.BudgetReportDTO;
import com.peter.financeapp.session.SessionManager;
import com.peter.financeapp.util.validator.ValidationException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BudgetService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final SessionManager sessionManager;

    public BudgetService(TransactionRepository transactionRepository,CategoryRepository categoryRepository,BudgetRepository budgetRepository,SessionManager sessionManager){
        this.transactionRepository = transactionRepository; this.categoryRepository = categoryRepository; this.budgetRepository = budgetRepository; this.sessionManager = sessionManager;
    }

    public void save(Budget budget){
        if(!sessionManager.isloggedIn()){
            throw new ValidationException("User must be logged in!");
        }
        if(budget.getAmount()==null){
            throw new ValidationException("Amount cannot be null");
        }
        if(budget.getCategoryId()==null){
            throw new ValidationException("Category cannot be null");
        }
        if (budget.getMonth()==null){
            throw new ValidationException("Month cannot be null");
        } try {
        budgetRepository.save(budget);} catch (DataAccessException e) {
            throw new ValidationException(e.getMessage());
        }

    }
    public void delete(Long id){
        if(!sessionManager.isloggedIn()){
            throw new ValidationException("User must be logged in!");
        }
        budgetRepository.delete(id);
    }
    public List<Budget> findUserBudgetForMonth(String month){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        return budgetRepository.findUserBudgets(sessionManager.getCurrentUser().getId(), month);
    }
    public Budget findUserBudgetForCategoryAndMonth(Long categoryId,String month){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        return budgetRepository.findByUserCategoryAndMonth(sessionManager.getCurrentUser().getId(), categoryId,month);
    }
    public void update(Budget budget){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        budgetRepository.update(budget);
    }

    public List<BudgetReportDTO> getBudgetsForMonth(String month){
        if(!sessionManager.isloggedIn()){
            throw new IllegalStateException("User must be logged in!");
        }
        List<Budget> budgets = budgetRepository.findUserBudgets(sessionManager.getCurrentUser().getId(), month);

        List<BudgetReportDTO> result = new ArrayList<>();
        Map<Long,BigDecimal> monthlyExpense = transactionRepository.getMonthlyCategorySpending(sessionManager.getCurrentUser().getId(), month.toString());

        for(Budget b: budgets) {

            BigDecimal spent = monthlyExpense.getOrDefault(b.getCategoryId(),BigDecimal.ZERO);
            Category c = categoryRepository.findById(b.getCategoryId());
            BigDecimal remaining = b.getAmount().subtract(spent);

            BudgetReportDTO dto = new BudgetReportDTO(c.getName(),b.getAmount(),spent,remaining);
            dto.setBudgetId(b.getId());
            if(spent.compareTo(b.getAmount())>0){
                dto.setStatus("EXCEEDED");
            } else if (spent.compareTo(b.getAmount().multiply(new BigDecimal("0.8")))>0){
                dto.setStatus("WARNING");
            } else {
                dto.setStatus("GOOD");
            }
            result.add(dto);

        }
        return result;
    }
    public Budget findBudget(Long id){
        return budgetRepository.findUserBudget(id);
    }
}
