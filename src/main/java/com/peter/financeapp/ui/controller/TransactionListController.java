package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.service.TransactionService;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.util.PDFExporter;
import com.peter.financeapp.util.SceneManager;
import com.peter.financeapp.util.Toast;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransactionListController extends BaseController {
    @FXML private ComboBox<YearMonth> monthBox;
    private YearMonth currentEndMonth;
    private boolean isUpdatingMonths = false;
    private static final int ROWS_PER_PAGE = 10;

    List<TransactionReportDTO> transactions = new ArrayList<>();

    @FXML private LineChart<Number,Number> transactionChart;
    @FXML private TableView<TransactionReportDTO> transactionsTable;
    @FXML private TableColumn<TransactionReportDTO, LocalDate> dateColumn;
    @FXML private TableColumn<TransactionReportDTO,String> categoryColumn;
    @FXML private TableColumn<TransactionReportDTO,String> typeColumn;
    @FXML private TableColumn<TransactionReportDTO, BigDecimal> amountColumn;
    @FXML private TableColumn<TransactionReportDTO,Void> actionColumn;

    @FXML
    private Pagination pagination;



    @FXML public void initialize(){
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("categoryType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        actionColumn.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {editBtn.setStyle(
                    "-fx-background-color: #2563eb;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Nunito';" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 5 10;" +
                            "-fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                TransactionReportDTO transaction = getTableView().getItems().get(getIndex());
                handleEdit(transaction);
            });

            }

            {deleteBtn.setStyle(
                    "-fx-background-color: #ef4444;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Nunito';" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 5 10;" +
                            "-fx-cursor: hand;"
            );
                deleteBtn.setOnAction(e -> {
                    TransactionReportDTO transaction = getTableView().getItems().get(getIndex());
                    handleDelete(transaction);
                });
            }

            @Override
            protected void updateItem(Void item,boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5,editBtn,deleteBtn);
                    setGraphic(box);
                }
            }
        });

    }
    @Override
    public void setSceneManager(SceneManager sceneManager) {
        super.setSceneManager(sceneManager);
        setupMonths();
        loadTransactions();

    }

    @Override
    public void setRootStack(StackPane rootStack) {
        super.setRootStack(rootStack);
    }

    private void loadTransactions(){
        transactions = sceneManager.getAppConfig().getTransactionService().getTransactionsForMonth(monthBox.getValue().toString());
        transactionsTable.getItems().setAll(transactions);
        loadChart(transactions);
        if(transactionsTable.getItems().isEmpty()){
            transactionsTable.setPlaceholder(new Label("No transactions found"));
        }
        setupPagination();
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
        loadTransactions();
    }

    private void handleDelete(TransactionReportDTO transaction){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Transaction");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            sceneManager.getAppConfig().getTransactionService().deleteTransaction(transaction.getTransactionId());
            loadTransactions();
        }
    }
    private void handleEdit(TransactionReportDTO transaction){
        sceneManager.openModal("/ui/fxml/transaction-form.fxml","Edit Transaction",controller->{
            TransactionController c = (TransactionController) controller;
            c.setType(transaction.getCategoryType().toString());
            c.setTransaction(transaction);
        });
        loadTransactions();
    }
    private void loadChart(List<TransactionReportDTO> transactions){

        transactionChart.getData().clear();

        Map<Integer,BigDecimal> incomeMap = new HashMap<>();
        Map<Integer,BigDecimal> expenseMap = new HashMap<>();

        for (TransactionReportDTO transaction : transactions){
            int day = transaction.getDate().getDayOfMonth();

            if (transaction.getCategoryType()== CategoryType.INCOME){
                incomeMap.put(day,incomeMap.getOrDefault(day,BigDecimal.ZERO).add(transaction.getAmount()));
            }
            else {
                expenseMap.put(day,expenseMap.getOrDefault(day,BigDecimal.ZERO).add(transaction.getAmount()));
            }

            XYChart.Series<Number,Number> incomeSeries = new XYChart.Series<>();
            incomeSeries.setName("Income");

            XYChart.Series<Number,Number> expenseSeries = new XYChart.Series<>();
            expenseSeries.setName("Expense");

            for (int d = 1;d <=31;d++){
                BigDecimal income = incomeMap.getOrDefault(d,BigDecimal.ZERO);
                BigDecimal expense = expenseMap.getOrDefault(d,BigDecimal.ZERO);

                incomeSeries.getData().add(new XYChart.Data<>(d,income));
                expenseSeries.getData().add(new XYChart.Data<>(d,expense));

                NumberAxis xAxis = (NumberAxis) transactionChart.getXAxis();
                xAxis.setAutoRanging(false);
                xAxis.setLowerBound(1);
                xAxis.setUpperBound(31);
                xAxis.setTickUnit(1);

                BigDecimal maxIncome = incomeMap.values().stream()
                                .max(BigDecimal::compareTo)
                                        .orElse(BigDecimal.ZERO);
                BigDecimal maxExpense = expenseMap.values().stream()
                                .max(BigDecimal::compareTo)
                                        .orElse(BigDecimal.ZERO);
                BigDecimal max = maxIncome.max(maxExpense);

                double maxValue = max.doubleValue();
                double upperBound = Math.ceil(maxValue/5000)*5000;

                NumberAxis yAxis = (NumberAxis) transactionChart.getYAxis();
                yAxis.setAutoRanging(false);
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(upperBound);
                yAxis.setTickUnit(upperBound/5);

                yAxis.setTickLabelFormatter(new StringConverter<>() {
                    @Override
                    public String toString(Number number) {
                        double value = number.doubleValue();

                        if (value>=1000) {
                            return "₦" + (int)(value/1000) + "k";
                        }
                        return String.valueOf(number.intValue());
                    }

                    @Override
                    public Number fromString(String s) {
                        return 0;
                    }
                });


                transactionChart.getData().clear();
                transactionChart.setLegendVisible(true);
                transactionChart.setCreateSymbols(true);
                transactionChart.setAnimated(false);
                transactionChart.getData().addAll(incomeSeries,expenseSeries);
            }
        }
    }
    private ObservableList<YearMonth> generateMonths(YearMonth endMonth){
        ObservableList<YearMonth> months = FXCollections.observableArrayList();
        for (int i = 5; i>=0;i--){
            months.add(endMonth.minusMonths(i));
        } return months;
    }
    private void setupMonths(){
        currentEndMonth = YearMonth.now();
        monthBox.setItems(generateMonths(currentEndMonth));
        monthBox.setValue(currentEndMonth); // always default to current month
        setupConverter();
        setupListener();
        monthBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(YearMonth item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null) ? item.format(DateTimeFormatter.ofPattern("MMMM yyyy")) : "");
            }
        });
    }

    private void setupListener() {
        monthBox.setOnAction(e -> {
            if (isUpdatingMonths) return; // block re-entry

            YearMonth selected = monthBox.getValue();
            if (selected == null) return;

            YearMonth oldest = monthBox.getItems().get(0);
            YearMonth newest = monthBox.getItems().get(monthBox.getItems().size() - 1);

            if (selected.equals(oldest)) {
                isUpdatingMonths = true;
                currentEndMonth = currentEndMonth.minusMonths(5);
                monthBox.setItems(generateMonths(currentEndMonth));
                monthBox.setValue(selected);
                isUpdatingMonths = false;

            } else if (selected.equals(newest)) {
                isUpdatingMonths = true;
                YearMonth newEnd = currentEndMonth.plusMonths(5);
                if (newEnd.isAfter(YearMonth.now())) {
                    newEnd = YearMonth.now();
                }
                currentEndMonth = newEnd;
                monthBox.setItems(generateMonths(currentEndMonth));
                monthBox.setValue(selected);
                isUpdatingMonths = false;
            }

            loadTransactions();
            pagination.setCurrentPageIndex(0);
        });
    }

    private void setupConverter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(YearMonth month) {
                return (month!=null) ? month.format(formatter):"";
            }

            @Override
            public YearMonth fromString(String s) {
                return null;
            }
        });
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) transactions.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateTablePage(newVal.intValue());
        });

        // Load first page immediately
        updateTablePage(0);
    }

    private void updateTablePage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, transactions.size());

        if (transactions.isEmpty()) {
            transactionsTable.setItems(FXCollections.observableArrayList());
        } else {
            transactionsTable.setItems(
                    FXCollections.observableArrayList(
                            transactions.subList(fromIndex, toIndex)
                    )
            );
        }
        transactionsTable.refresh();
    }
    @FXML
    private void handleExportPdf() {
        // Let user choose where to save
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction Report");
        fileChooser.setInitialFileName("FinPulse_Transactions_" +
                monthBox.getValue().toString() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        Stage stage = (Stage) rootStack.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                String userName = sceneManager.getAppConfig().getSessionManager().getCurrentUser().getFirstName();
                String month = monthBox.getValue()
                        .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"));

                PDFExporter.exportTransactions(
                        transactions, file.getAbsolutePath(), userName, month);

                Toast.show(rootStack, "PDF exported successfully!");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.show(rootStack, "Export failed. Please try again.");
            }
        }
    }


}
