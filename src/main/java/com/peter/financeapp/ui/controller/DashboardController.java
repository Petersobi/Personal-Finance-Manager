package com.peter.financeapp.ui.controller;

import com.peter.financeapp.service.DashboardService;
import com.peter.financeapp.service.dashboard.dto.DashboardSummaryDTO;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardController extends BaseController {

    @FXML private Label firstNameLabel;
    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label savingsLabel;

    @FXML private TableView<TransactionReportDTO> recentTransactionsTable;
    @FXML private TableColumn<TransactionReportDTO, LocalDate> dateColumn;
    @FXML private TableColumn<TransactionReportDTO,String> categoryColumn;
    @FXML private TableColumn<TransactionReportDTO,String> typeColumn;
    @FXML private TableColumn<TransactionReportDTO, BigDecimal> amountColumn;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;
        loadDashboard();
    }

    @FXML public void initialize(){
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("categoryType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

    }
   public void loadDashboard(){
        firstNameLabel.setText(sceneManager.getAppConfig().getSessionManager().getCurrentUser().getFirstName());

        DashboardService dashboardService = sceneManager.getAppConfig().getDashboardService();
       DashboardSummaryDTO dashboardSummary = dashboardService.getDashboardSummary();

       incomeLabel.setText(formatCurrency(dashboardSummary.getMonthlyIncome()));
       expenseLabel.setText(formatCurrency(dashboardSummary.getMonthlyExpense()));
       savingsLabel.setText(formatCurrency(dashboardSummary.getSavings()));

       List<TransactionReportDTO> recentTransactions = dashboardSummary.getRecentTransactions();

       recentTransactionsTable.getItems().setAll(recentTransactions);
       if(recentTransactionsTable.getItems().isEmpty()){
           recentTransactionsTable.setPlaceholder(
                   new Label("No recent transactions")
           );
       }
   }
    @FXML private void handleAddIncome(){
        sceneManager.openModal("/ui/fxml/transaction-form.fxml","Add Income",controller->{
            TransactionController c = (TransactionController) controller;
            c.setType("INCOME");
        });
        refreshAfterClose();
    }
    @FXML private void handleAddExpense(){
        sceneManager.openModal("/ui/fxml/transaction-form.fxml","Add Expense",controller->{
            TransactionController c = (TransactionController) controller;
            c.setType("EXPENSE");
        });
        refreshAfterClose();
    }
    private void refreshAfterClose(){
        loadDashboard();
    }
   private String formatCurrency(BigDecimal amount){
        return String.format("₦%,.2f",amount);
   }
}
