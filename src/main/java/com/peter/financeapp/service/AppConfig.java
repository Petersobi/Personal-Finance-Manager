package com.peter.financeapp.service;

import com.peter.financeapp.dao.SQLIteCategoryDAO;
import com.peter.financeapp.dao.SQLiteBudgetDAO;
import com.peter.financeapp.dao.SQLiteTransactionDAO;
import com.peter.financeapp.dao.SQLiteUserDAO;
import com.peter.financeapp.repository.BudgetRepository;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.repository.TransactionRepository;
import com.peter.financeapp.service.alert.AlertService;
import com.peter.financeapp.service.security.PasswordEncoder;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.service.security.BcryptPasswordEncoder;
import com.peter.financeapp.session.SessionManager;
import com.peter.financeapp.util.validator.*;

public class AppConfig {

    private  final SessionManager sessionManager ;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private  final CategoryRepository categoryRepository;
    private  final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionValidator transactionValidator;
    private final CategoryValidator categoryValidator;
    private final BudgetValidator budgetValidator;
    private final LoginValidator loginValidator;
    private final RegistrationValidator registrationValidator;

    private final AuthService authService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final ReportService reportService;
    private final DashboardService dashboardService;
    private final BudgetService budgetService;


    public AppConfig(){

        sessionManager = new SessionManager();

        userRepository = new SQLiteUserDAO();
        transactionRepository = new SQLiteTransactionDAO();
        categoryRepository = new SQLIteCategoryDAO();
        budgetRepository = new SQLiteBudgetDAO();
        passwordEncoder = new BcryptPasswordEncoder();
        transactionValidator = new TransactionValidator();
        categoryValidator = new CategoryValidator();
        budgetValidator = new BudgetValidator();
        loginValidator = new LoginValidator();
        registrationValidator = new RegistrationValidator();

        authService = new AuthService(userRepository,passwordEncoder,sessionManager);
        transactionService = new TransactionService(transactionRepository,categoryRepository,budgetRepository,sessionManager);
        categoryService = new CategoryService(categoryRepository,transactionRepository,sessionManager);
        reportService = new ReportService(transactionRepository,sessionManager);
        dashboardService = new DashboardService(transactionService,reportService,sessionManager);
        budgetService = new BudgetService(transactionRepository,categoryRepository,budgetRepository,sessionManager);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public BudgetRepository getBudgetRepository() {
        return budgetRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public DashboardService getDashboardService() {
        return dashboardService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public TransactionValidator getTransactionValidator() {
        return transactionValidator;
    }

    public CategoryValidator getCategoryValidator() {
        return categoryValidator;
    }

    public BudgetValidator getBudgetValidator() {
        return budgetValidator;
    }

    public LoginValidator getLoginValidator() {
        return loginValidator;
    }

    public RegistrationValidator getRegistrationValidator() {
        return registrationValidator;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
    public BudgetService getBudgetService(){
        return budgetService;
    }
}
