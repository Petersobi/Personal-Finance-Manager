package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.service.report.dto.CategoryReportDTO;
import com.peter.financeapp.util.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CategoryController extends BaseController {

    @FXML private  PieChart categoryChart;
    @FXML private ListView<CategoryReportDTO> categoryListView;
    private List<CategoryReportDTO> masterList;
    @FXML private ComboBox<YearMonth> monthBox;
    private YearMonth currentEndMonth;
    private boolean isUpdatingMonths = false;
    private static final int ROWS_PER_PAGE = 10;

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category,String> nameColumn;
    @FXML private TableColumn<Category,String> typeColumn;
    @FXML private TableColumn<Category,Void> actionColumn;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Pagination pagination;
    @FXML private StackPane root;
    Map<String,CategoryReportDTO> dataMap = new HashMap<>();
    List<Category> categories = new ArrayList<>();

    private SceneManager sceneManager;
    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;

        setupTable();
        setupFilter();
        setupMonths();
        setupCellFactory();
        loadCategories();

    }
    private void setupTable(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
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
                    Category category = getTableView().getItems().get(getIndex());
                    handleEdit(category);
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
                    Category category = getTableView().getItems().get(getIndex());
                    handleDelete(category);
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
    private void setupFilter(){
        filterComboBox.getItems().addAll("ALL","INCOME","EXPENSE");
        filterComboBox.setValue("ALL");
        filterComboBox.setOnAction(e-> loadCategories());
    }
    private void loadCategories() {
        String filter = filterComboBox.getValue();


        if (filter.equals("ALL")) {
            categories = sceneManager.getAppConfig().getCategoryService().getUserCategories();
        } else {
            categories = sceneManager.getAppConfig().getCategoryService().getCategoryByType(filter);
        }
        categoryTable.getItems().setAll(categories);
        if(categoryTable.getItems().isEmpty()){
            categoryTable.setPlaceholder(
                    new Label("No category found")
            );
        }

        List<CategoryReportDTO> categoriesReport = sceneManager.getAppConfig().getCategoryService().getCategoryBreakDown(monthBox.getValue().toString());
        setUpChart(categoriesReport);
        loadData();
        setupPagination();

    }
    @FXML  private void handleAddCategory(){
        sceneManager.openModal("/ui/fxml/category-form.fxml","Add Category",controller->{
            CategoryFormController c = (CategoryFormController) controller;
            c.setSceneManager(sceneManager);
        });
        loadCategories();
    }
    private void handleEdit(Category category){
        sceneManager.openModal("/ui/fxml/category-form.fxml","Edit Category",controller->{
            CategoryFormController c = (CategoryFormController) controller;
            c.setCurrentCategory(category);
        });
        loadCategories();
    }
    private void handleDelete(Category category){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            sceneManager.getAppConfig().getCategoryService().delectCategory(category.getId());
            loadCategories();
        }
    }
    private void loadData() {
        masterList = sceneManager.getAppConfig().getCategoryService().getCategoryBreakDown(monthBox.getValue().toString());

        updateCategoryList();
    }
    private void updateCategoryList() {

        String selected = filterComboBox.getValue();

        List<CategoryReportDTO> filtered;

        if ("ALL".equals(selected)) {
            filtered = masterList;

        } else {
            filtered = masterList.stream()
                    .filter(c -> c.getCategoryType().name().equals(selected))
                    .toList();
        }

        // sort descending
        filtered = filtered.stream()
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .toList();

        categoryListView.setItems(FXCollections.observableArrayList(filtered));
    }
    private void setupCellFactory() {

        categoryListView.setCellFactory(list -> new ListCell<>() {

            private final HBox root = new HBox(10);
            private final VBox textBox = new VBox(5);

            private final Label nameLabel = new Label();
            private final Label typeLabel = new Label();
            private final Label amountLabel = new Label();

            private final ProgressBar bar = new ProgressBar();

            {
                root.setFillHeight(true);
                root.setAlignment(Pos.CENTER_LEFT);

                textBox.setSpacing(4);

                nameLabel.getStyleClass().add("list-title");
                typeLabel.getStyleClass().add("list-subtitle");
                amountLabel.getStyleClass().add("list-amount");
                bar.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(bar, Priority.ALWAYS);

                textBox.getChildren().addAll(nameLabel, typeLabel, bar);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                root.getChildren().addAll(textBox, spacer, amountLabel);
            }

            @Override
            protected void updateItem(CategoryReportDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(item.getCategoryName());
                typeLabel.setText(item.getCategoryType().toString());

                // 🔥 Color by type
                if (item.getCategoryType().name().equals("INCOME")) {
                    typeLabel.setStyle("-fx-text-fill: #16a34a;");
                } else {
                    typeLabel.setStyle("-fx-text-fill: #dc2626;");
                }

                amountLabel.setText("₦" +
                        String.format("%,.0f", item.getTotalAmount().doubleValue()));

                // 🔥 Normalize progress (per type)
                double max = categoryListView.getItems().stream()
                        .filter(c -> c.getCategoryType().equals(item.getCategoryType()))
                        .map(CategoryReportDTO::getTotalAmount)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ONE)
                        .doubleValue();

                double progress = item.getTotalAmount().doubleValue() / max;
                bar.setProgress(progress);

                setGraphic(root);
            }
        });
    }
    private void setUpChart(List<CategoryReportDTO> data){
        dataMap.clear();
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        for (CategoryReportDTO d : data) {


            if(filterComboBox.getValue().equals("ALL")){
                chartData.add(new PieChart.Data(d.getCategoryName(),d.getTotalAmount().doubleValue()));
                dataMap.put(d.getCategoryName(),d);}
            else if (filterComboBox.getValue().equals("INCOME")) {
                if(d.getCategoryType().equals(CategoryType.INCOME)){
                    chartData.add(new PieChart.Data(d.getCategoryName(),d.getTotalAmount().doubleValue()));
                    dataMap.put(d.getCategoryName(),d);
                }
            } else if (filterComboBox.getValue().equals("EXPENSE") ){
                if (d.getCategoryType().equals(CategoryType.EXPENSE)){
                    chartData.add(new PieChart.Data(d.getCategoryName(),d.getTotalAmount().doubleValue()));
                    dataMap.put(d.getCategoryName(),d);}
            }
        }
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



    private ObservableList<YearMonth> generateMonths(YearMonth endMonth){
        ObservableList<YearMonth> months = FXCollections.observableArrayList();
        for (int i = 5; i>=0;i--){
            months.add(endMonth.minusMonths(i));
        } return months;
    }
    private void setupMonths(){
        currentEndMonth = YearMonth.now();

        monthBox.setItems(generateMonths(currentEndMonth));
        monthBox.setValue(currentEndMonth);

        setupConverter();
        setupListener();
        monthBox.setButtonCell(new ListCell<>(){
            @Override
            protected void  updateItem(YearMonth item,boolean empty){
                super.updateItem(item, empty);
                if (item!=null){
                    setText(item.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
                }
            }
        });
    }

    private void setupListener() {
        monthBox.setOnAction(e->{
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

            loadData(); loadCategories();
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
        int pageCount = (int) Math.ceil((double) categories.size() / ROWS_PER_PAGE);
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
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, categories.size());

        if (categories.isEmpty()) {
            categoryTable.setItems(FXCollections.observableArrayList());
        } else {
            categoryTable.setItems(
                    FXCollections.observableArrayList(
                            categories.subList(fromIndex, toIndex)
                    )
            );
        }
        categoryTable.refresh();
    }

}