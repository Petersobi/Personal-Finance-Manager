package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Budget;
import com.peter.financeapp.model.Category;
import com.peter.financeapp.util.SceneManager;
import com.peter.financeapp.util.Toast;
import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.validationForm.BudgetForm;
import com.peter.financeapp.util.validator.CategoryValidator;
import com.peter.financeapp.util.validator.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

public class BudgetFormController extends BaseController {

    @FXML private StackPane root;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Category> chooseCategoryBox;

    @FXML private Label amountLabel;
    @FXML private Label dateLabel;
    @FXML private Label categoryLabel;

    private Budget currentBudget;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        super.setSceneManager(sceneManager);
        setDatePicker(datePicker);
        setChooseCategoryBox(chooseCategoryBox);
        formatAmountField(amountField);

    }
    @Override
    public void setRootStack(StackPane rootStack) {
        super.setRootStack(rootStack);
    }

    @FXML private void handleSave() {
        String amount = amountField.getText().replaceAll(",","");
        BudgetForm form =  new BudgetForm();

        form.setCategoryId(chooseCategoryBox.getValue()!=null ? chooseCategoryBox.getValue().getId() : null);
        form.setAmount(amount);

        Long userId = sceneManager.getAppConfig().getSessionManager().getCurrentUser().getId();
        if(currentBudget==null){
            form.setMonth(datePicker.getValue());
        } else {
            form.setMonth(LocalDate.parse(currentBudget.getMonth()));
        }
        ValidationResult result = sceneManager.getAppConfig().getBudgetValidator().validate(form);

        if (result.hasErrors()){
            showValidationErrors(result);
            return;
        }
        try {
        if(currentBudget==null){
            String month = datePicker.getValue().toString();
        sceneManager.getAppConfig().getBudgetService().save(new Budget(userId, form.getCategoryId(), form.getMonth().toString(),new BigDecimal(form.getAmount())));
            Toast.show(rootStack,"Budget saved!");}
        else {
            sceneManager.getAppConfig().getBudgetService().update(new Budget(userId, form.getCategoryId(), currentBudget.getMonth(),new BigDecimal(form.getAmount())));
            Toast.show(rootStack,"Budget updated!");
        }

        closeWindow();
        } catch (ValidationException e) {
            if (e.getMessage().toLowerCase().contains("unique")){
                Budget budget = new Budget(userId, form.getCategoryId(), form.getMonth().toString(),new BigDecimal(form.getAmount()));
                updateBudget(budget);
                closeWindow();
            } else
           Toast.show(root,e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }
    private void closeWindow(){
        Stage stage=(Stage) chooseCategoryBox.getScene().getWindow();
        stage.close();
    }
    
    private void setDatePicker(DatePicker datePicker){
        datePicker.setConverter(new StringConverter<>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            @Override
            public String toString(LocalDate date) {
                return (date !=null)? formatter.format(date):"";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null&& !string.isEmpty()) ? YearMonth.parse(string,formatter).atDay(1):null;
            }
        });
    }
    private void setChooseCategoryBox(ComboBox<Category> categoryBox){
        categoryBox.getItems().setAll(sceneManager.getAppConfig().getCategoryService().getUserCategories());
    }

    public void setCurrentBudget(Budget currentBudget) {
        this.currentBudget = currentBudget;
        amountField.setText(currentBudget.getAmount().toString());
        chooseCategoryBox.setValue(sceneManager.getAppConfig().getCategoryService().findById(currentBudget.getCategoryId()));
        chooseCategoryBox.setDisable(true);
        datePicker.setDisable(true);
    }
    private void showValidationErrors(ValidationResult result){
        clearErrors();

        if(result.getError("amount")!=null){
            amountField.setStyle("-fx-border-color: red;");
            amountLabel.setText(result.getError("amount"));
            amountLabel.setVisible(true);
        }
        if(result.getError("date")!=null) {
            datePicker.setStyle("-fx-border-color: red;");
            dateLabel.setText(result.getError("date"));
            dateLabel.setVisible(true);
        }
        if(result.getError("category")!=null) {
            chooseCategoryBox.setStyle("-fx-border-color: red;");
            categoryLabel.setText(result.getError("category"));
            categoryLabel.setVisible(true);
        }
    }
    private void clearErrors(){
        amountField.setStyle("");
        amountLabel.setVisible(false);
        datePicker.setStyle("");
        dateLabel.setVisible(false);
        chooseCategoryBox.setStyle("");
        categoryLabel.setVisible(false);
    }
    private void formatAmountField(TextField amountField){
        amountField.textProperty().addListener((obs,oldVal,newVal)->{
            String digits = newVal.replaceAll("[^\\d]","");

            if(digits.isEmpty()){
                amountField.setText("");
                return;
            }
            String formatted = String.format("%,d",Long.parseLong(digits));

            if(!newVal.equals(formatted)){
                amountField.setText(formatted);
                amountField.positionCaret(formatted.length());
            }
        });
    }
    private void updateBudget(Budget budget){
        Category category = sceneManager.getAppConfig().getCategoryRepository().findById(budget.getCategoryId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Budget Already Exists");
        alert.setHeaderText("A budget for this category already exists");
        alert.setContentText(
                "You already have a budget for \"" + category.getName() + "\" in " +
                        budget.getMonth() + ".\n\nDo you want to update it to ₦" +
                        String.format("%,.0f", budget.getAmount()) + "?"
        );

        // Style the buttons
        ButtonType updateButton = new ButtonType("Yes, Update");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(updateButton, cancelButton);

        // Apply dark theme
        styleAlert(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == updateButton) {
            try {
                sceneManager.getAppConfig().getBudgetService().update(budget);
                Toast.show(rootStack, "Budget updated!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();

        // Apply stylesheet
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/app-theme.css").toExternalForm()
        );

        // Dialog pane
        dialogPane.setStyle(
                "-fx-background-color: #1f2937;" +
                        "-fx-border-color: #374151;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );

        // Header
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-background-radius: 12 12 0 0;"
        );

        // Header text
        dialogPane.lookup(".header-panel .label").setStyle(
                "-fx-text-fill: #f9fafb;" +
                        "-fx-font-family: 'Syne Bold';" +
                        "-fx-font-size: 15px;"
        );

        // Content text
        dialogPane.lookup(".content.label").setStyle(
                "-fx-text-fill: #9ca3af;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-size: 13px;"
        );

        // Update button — green
        dialogPane.lookupButton(alert.getButtonTypes().get(0)).setStyle(
                "-fx-background-color: linear-gradient(to right, #10b981, #059669);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;"
        );

        // Cancel button — outline
        dialogPane.lookupButton(alert.getButtonTypes().get(1)).setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #9ca3af;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-border-color: #374151;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;"
        );

        // Stage background
        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.initStyle(StageStyle.UNDECORATED);
    }
}
