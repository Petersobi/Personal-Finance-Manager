package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.service.report.dto.*;
import com.peter.financeapp.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportController extends BaseController {

    @FXML private VBox budgetContainer;

    @FXML private LineChart<Number,Number> transactionChart;
    @FXML private PieChart categoryChart;
    @FXML private ListView<BudgetReportDTO> budgetListView;

    @FXML private Label balanceLabel;
    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label savingsLabel;

    @FXML private ComboBox<Object> monthPicker;

    Map<String,CategoryReportDTO> dataMap = new HashMap<>();

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        super.setSceneManager(sceneManager);
        setupMonth();
        loadMonthlyReport(monthPicker.getValue());

    }

    private void setupMonth(){
        YearMonth now = YearMonth.now();
        monthPicker.getItems().add(now.getYear());


        for (int i = 0; i < 12; i++) {
            monthPicker.getItems().add(now.minusMonths(i));
        }

        monthPicker.setValue(now); // default = current month

        setupFormatter();
        setupDynamicLoading();
        monthPicker.setOnAction(e -> {

            Object selected = monthPicker.getValue();

            if (selected instanceof YearMonth ym) {

                loadMonthlyReport(ym);

            } else if (selected instanceof Integer year) {

                loadYearlyReport(year);
            }
        });

    }
    private void setupFormatter() {

        monthPicker.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);

                } else if (item instanceof YearMonth ym) {
                    setText(ym.getMonth().toString() + " " + ym.getYear());

                } else if (item instanceof Integer year) {
                    setText(String.valueOf(year)); // e.g 2026
                }
            }
        });

        monthPicker.setButtonCell(monthPicker.getCellFactory().call(null));
    }

    private void setupDynamicLoading() {

        monthPicker.setOnShowing(e -> {

            Object lastItem = monthPicker.getItems()
                    .get(monthPicker.getItems().size() - 1);

            if (lastItem instanceof YearMonth lastMonth) {

                    Set<YearMonth> existingMonths = new HashSet<>();

                    for (Object item : monthPicker.getItems()) {
                        if (item instanceof YearMonth ym) {
                            existingMonths.add(ym);
                        }
                    }
                    for (int i = 1; i <= 6; i++) {
                        YearMonth newMonth = lastMonth.minusMonths(i);

                        if (!existingMonths.contains(newMonth)) {
                            monthPicker.getItems().add(newMonth);
                        }
                    }
                }
        });



    }


    private void loadMonthlyReport(Object ym){
        budgetListView.setVisible(true);


        MonthlySummaryDTO report = sceneManager.getAppConfig().getReportService().getMonthlySummary(ym.toString());

        balanceLabel.setText(formatCurrency(report.getTotalIncome().subtract(report.getTotalExpense())));
        incomeLabel.setText(formatCurrency(report.getTotalIncome()));
        expenseLabel.setText(formatCurrency(report.getTotalExpense()));
        savingsLabel.setText(formatCurrency(report.getNetSavings()));

        List<CategoryReportDTO> categories = sceneManager.getAppConfig().getReportService().getTotalBreakdown(ym.toString());
        loadChart(categories);

        List<TransactionReportDTO> transactions = sceneManager.getAppConfig().getTransactionService().getTransactionsForMonth(ym.toString());
        loadLineChartForMonth(transactions);

        List<BudgetReportDTO> budgets = sceneManager.getAppConfig().getBudgetService().getBudgetsForMonth(ym.toString());
        loadBudgets(budgets);


    }
    private void loadYearlyReport(Object year){
        budgetContainer.setVisible(false);

        MonthlySummaryDTO report = sceneManager.getAppConfig().getReportService().getMonthlySummary(year.toString());

        balanceLabel.setText(formatCurrency(report.getTotalIncome().subtract(report.getTotalExpense())));
        incomeLabel.setText(formatCurrency(report.getTotalIncome()));
        expenseLabel.setText(formatCurrency(report.getTotalExpense()));
        savingsLabel.setText(formatCurrency(report.getNetSavings()));

        List<CategoryReportDTO> categories = sceneManager.getAppConfig().getReportService().getTotalBreakdown(year.toString());
        loadChart(categories);

        List<TransactionReportDTO> transactions = sceneManager.getAppConfig().getTransactionService().getTransactionsForMonth(year.toString());
        loadLIneChartForYear(transactions);


    }
    private String formatCurrency(BigDecimal amount){
        return String.format("N%,.2f",amount);
    }

    private void loadChart(List<CategoryReportDTO> data){
        dataMap.clear();
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        for (CategoryReportDTO d : data) {
                chartData.add(new PieChart.Data(d.getCategoryName(),d.getTotalAmount().doubleValue()));
                dataMap.put(d.getCategoryName(),d);}

        categoryChart.setData(chartData);

        Platform.runLater(() -> applyColors(chartData));
    }
    private void applyColors(ObservableList<PieChart.Data> data) {

        List<PieChart.Data> sorted = data.stream()
                .sorted((a, b) -> Double.compare(b.getPieValue(), a.getPieValue()))
                .toList();

        int size = sorted.size();

        for (int i = 0; i < size; i++) {

            PieChart.Data d = sorted.get(i);

            CategoryReportDTO item = dataMap.get(d.getName());

            double factor = (double) i / size; // 0 → 1

            String color;

            if ("INCOME".equals(item.getCategoryType().name())) {
                color = getGreenShade(factor);
            } else {
                color = getRedShade(factor);
            }

            d.getNode().setStyle("-fx-pie-color: " + color + ";");
        }
    }


    private String getGreenShade(double factor) {

        int r = (int)(50 + factor * 100);   // lighter → darker
        int g = (int)(180 + factor * 60);
        int b = (int)(80 + factor * 80);

        return String.format("rgb(%d,%d,%d)", r, g, b);
    }

    private String getRedShade(double factor) {

        int r = (int)(180 + factor * 60);
        int g = (int)(60 + factor * 60);
        int b = (int)(60 + factor * 60);

        return String.format("rgb(%d,%d,%d)", r, g, b);
    }


    private void loadLineChartForMonth(List<TransactionReportDTO> transactions){


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

                xAxis.setTickLabelFormatter(null);
                xAxis.setLabel("Day");

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

    private void loadLIneChartForYear(List<TransactionReportDTO> transactions) {

        Map<Integer, BigDecimal> incomeMap = new HashMap<>();
        Map<Integer, BigDecimal> expenseMap = new HashMap<>();


        for (TransactionReportDTO transaction : transactions) {

            int month = transaction.getDate().getMonthValue();

            if (transaction.getCategoryType() == CategoryType.INCOME) {
                incomeMap.put(month,
                        incomeMap.getOrDefault(month, BigDecimal.ZERO)
                                .add(transaction.getAmount()));
            } else {
                expenseMap.put(month,
                        expenseMap.getOrDefault(month, BigDecimal.ZERO)
                                .add(transaction.getAmount()));
            }
        }


        XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");


        for (int m = 1; m <= 12; m++) {

            BigDecimal income = incomeMap.getOrDefault(m, BigDecimal.ZERO);
            BigDecimal expense = expenseMap.getOrDefault(m, BigDecimal.ZERO);

            incomeSeries.getData().add(new XYChart.Data<>(m, income));
            expenseSeries.getData().add(new XYChart.Data<>(m, expense));
        }


        NumberAxis xAxis = (NumberAxis) transactionChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);

        xAxis.setTickLabelFormatter(null);
        xAxis.setLabel("Month");


        xAxis.setTickLabelFormatter(new StringConverter<>() {
            private final String[] months = {
                    "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };

            @Override
            public String toString(Number number) {
                int m = number.intValue();
                return (m >= 1 && m <= 12) ? months[m] : "";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });


        BigDecimal maxIncome = incomeMap.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxExpense = expenseMap.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal max = maxIncome.max(maxExpense);

        double maxValue = max.doubleValue();
        double upperBound = Math.ceil(maxValue / 5000) * 5000;

        NumberAxis yAxis = (NumberAxis) transactionChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(upperBound / 5);

        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                double value = number.doubleValue();

                if (value >= 1000) {
                    return "₦" + (int) (value / 1000) + "k";
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
        transactionChart.setCreateSymbols(false);
        transactionChart.setAnimated(false);

        transactionChart.getData().addAll(incomeSeries, expenseSeries);
    }
    private void loadBudgets(List<BudgetReportDTO> budgets) {

        budgetListView.setItems(FXCollections.observableArrayList(budgets));

        setupCellFactory();
    }

    private void setupCellFactory() {

        budgetListView.setCellFactory(list -> new ListCell<>() {

            private final VBox root = new VBox(8);
            private final HBox topRow = new HBox();
            private final Label nameLabel = new Label();
            private final Label amountLabel = new Label();
            private final ProgressBar bar = new ProgressBar();
            private final Label percentLabel = new Label();

            {
                // Use styleClass instead of inline styles
                nameLabel.getStyleClass().add("list-title");
                amountLabel.getStyleClass().add("list-amount");
                percentLabel.getStyleClass().add("list-subtitle");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                topRow.getChildren().addAll(nameLabel, spacer, amountLabel);

                bar.setPrefHeight(8);
                bar.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(bar, Priority.ALWAYS);

                root.getChildren().addAll(topRow, bar, percentLabel);
                root.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(root, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(BudgetReportDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                double spent = item.getSpentAmount().doubleValue();
                double budget = item.getBudgetAmount().doubleValue();
                double remaining = budget - spent;
                double progress = budget == 0 ? 0 : spent / budget;

                nameLabel.setText(item.getCategoryName());

                // ✅ Formatted correctly — no .0, with commas
                amountLabel.setText(
                        String.format("₦%,.0f / ₦%,.0f (₦%,.0f left)",
                                spent, budget, remaining)
                );

                if (progress > 1) {
                    percentLabel.setText((int)(progress * 100) + "% ⚠ Over budget");
                    percentLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-family: 'Nunito';");
                } else {
                    percentLabel.setText((int)(progress * 100) + "% used");
                    percentLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-family: 'Nunito';");
                }

                // Animated progress bar
                Timeline t = new Timeline(
                        new KeyFrame(Duration.seconds(0.5),
                                new KeyValue(bar.progressProperty(), Math.min(progress, 1)))
                );
                t.play();

                // Dynamic color
                String color;
                if (progress < 0.7) {
                    color = "#16a34a";
                } else if (progress <= 1.0) {
                    color = "#f59e0b";
                } else {
                    color = "#dc2626";
                }

                bar.setStyle("-fx-accent: " + color + ";");

                setGraphic(root);
            }
        });
    }



}
