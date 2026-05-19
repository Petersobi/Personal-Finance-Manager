package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Budget;
import com.peter.financeapp.model.Category;
import com.peter.financeapp.service.report.dto.BudgetReportDTO;
import com.peter.financeapp.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetController extends BaseController {
    @FXML private TableColumn<BudgetReportDTO,Void> actionColumn;

    @FXML private TableColumn<BudgetReportDTO, BigDecimal> budgetColumn;
    @FXML private TableColumn<BudgetReportDTO,String> categoryColumn;
    @FXML private TableView<BudgetReportDTO> budgetTable;

    @FXML private ListView<BudgetReportDTO> budgetListView;

    @FXML private Pagination pagination;

    @FXML private ComboBox<YearMonth> monthBox;
    private YearMonth currentEndMonth;
    private boolean isUpdatingMonths = false;
    private static final int ROWS_PER_PAGE = 10;
    List<BudgetReportDTO> budgets = new ArrayList<>();

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;
        setupTable();
        setupMonths();
        loadBudgets();

    }
    private void setupTable(){
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budgetAmount"));

        actionColumn.setCellFactory(col -> new TableCell<>(){
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {editBtn.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                editBtn.setOnAction(e -> {
                    BudgetReportDTO dto = getTableView().getItems().get(getIndex());
                    Budget budget = sceneManager.getAppConfig().getBudgetRepository().findUserBudget(dto.getBudgetId());
                    handleEdit(budget);
                });

            }

            {deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    BudgetReportDTO dto = getTableView().getItems().get(getIndex());
                    Budget budget = sceneManager.getAppConfig().getBudgetRepository().findUserBudget(dto.getBudgetId());
                    handleDelete(budget);
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

            loadBudgets();
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
    private void loadBudgets(){
        YearMonth selectedMonth = monthBox.getValue();
        budgets = sceneManager.getAppConfig().getBudgetService().getBudgetsForMonth(selectedMonth.toString());
        budgetTable.getItems().setAll(budgets);
        loadBudgets(budgets);
        setupPagination();
    }


    @FXML private void handleAddBudget() {
        sceneManager.openModal("/ui/fxml/budget-form.fxml","Add Budget",controller->{
            BudgetFormController c = (BudgetFormController) controller;
            c.setSceneManager(sceneManager);

        });
        loadBudgets();
    }
    private void handleEdit(Budget budget){
        sceneManager.openModal("/ui/fxml/budget-form.fxml","Edit Budget",controller->{
            BudgetFormController c = (BudgetFormController) controller;
            c.setCurrentBudget(budget);
        });
        loadBudgets();
    }
    private void handleDelete(Budget budget){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Budget");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            sceneManager.getAppConfig().getBudgetService().delete(budget.getId());
            loadBudgets();
        }
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

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) budgets.size() / ROWS_PER_PAGE);
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
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, budgets.size());

        if (budgets.isEmpty()) {
            budgetTable.setItems(FXCollections.observableArrayList());
        } else {
            budgetTable.setItems(
                    FXCollections.observableArrayList(
                            budgets.subList(fromIndex, toIndex)
                    )
            );
        }
        budgetTable.refresh();
    }


}
