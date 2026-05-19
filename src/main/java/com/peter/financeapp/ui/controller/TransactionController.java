package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.util.SceneManager;
import com.peter.financeapp.util.Toast;
import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.validationForm.TransactionForm;
import com.peter.financeapp.util.validator.ValidationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionController extends BaseController {
    @FXML private StackPane root;
    @FXML private ComboBox<Category> categoryBox;
    @FXML private TextField amountField;
    @FXML private TextField descrField;
    @FXML private DatePicker datePicker;

    @FXML private Label categoryLabel;
    @FXML private Label amountLabel;
    @FXML private Label dateLabel;

    private TransactionReportDTO currentTransaction;

    private String type;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;
        setupDatePicker(datePicker);
        formatAmountField(amountField);
    }

    @Override
    public void setRootStack(StackPane rootStack) {
        super.setRootStack(rootStack);
    }

    private void loadCategories(){
        List<Category> categories = sceneManager.getAppConfig().getCategoryService().getCategoryByType(type);
        categoryBox.getItems().setAll(categories);
    }
    @FXML private void handleSave(){
        String amount = amountField.getText().replaceAll(",","");

        TransactionForm form = new TransactionForm();
        form.setAmount(amount);
        form.setCategoryID(categoryBox.getValue()!=null ? categoryBox.getValue().getId() : null);
        form.setDescription(descrField.getText());
        form.setDate(datePicker.getValue());

        ValidationResult result = sceneManager.getAppConfig().getTransactionValidator().validate(form);

        if (result.hasErrors()){
            showValidationErrors(result);
            return;
        }
        try {

            if (currentTransaction == null){
            sceneManager.getAppConfig().getTransactionService().addTransaction(form.getCategoryID(),new BigDecimal(form.getAmount()), form.getDescription(),form.getDate(), LocalDateTime.now());
                Toast.show(rootStack, "Transaction Saved");}
            else {
                sceneManager.getAppConfig().getTransactionService().updateTransaction(currentTransaction.getTransactionId(),form.getCategoryID(),new BigDecimal(form.getAmount()), form.getDescription(),form.getDate());
                Toast.show(rootStack, "Transaction Updated");
            }



           closeWindow();

        } catch (ValidationException e) {
            Toast.show(root,e.getMessage());
        }
    }
    @FXML private void handleCancel(){
        closeWindow();
    }
    private void closeWindow(){
        Stage stage=(Stage) categoryBox.getScene().getWindow();
        stage.close();
    }
    @FXML private void handleAddCategory(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Category");
        dialog.setHeaderText("Create New Category");

// Apply your dark theme to the dialog
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/app-theme.css").toExternalForm()
        );

// Style the dialog pane itself
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #1f2937;" +
                        "-fx-border-color: #374151;" +
                        "-fx-border-width: 1;"
        );

// Style the header
        dialog.getDialogPane().lookup(".header-panel").setStyle(
                "-fx-background-color: #111827;"
        );

// Style the header text
        dialog.getDialogPane().lookup(".header-panel .label").setStyle(
                "-fx-text-fill: #f9fafb;" +
                        "-fx-font-family: 'Syne Bold';" +
                        "-fx-font-size: 15px;"
        );

// Style the text field inside
        TextField inputField = dialog.getEditor();

        // Grab the OK button
        javafx.scene.control.Button okButton =
                (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

// Disable it initially since field starts empty
        okButton.setDisable(true);

// Only enable when field has text
        dialog.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            okButton.setDisable(newVal.trim().isEmpty());
        });

        inputField.setPromptText("Enter category name");
        inputField.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: #f3f4f6;" +
                        "-fx-border-color: #374151;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-size: 13px;"
        );

// Style the buttons
        dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;"
        );

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
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
        Platform.runLater(() -> {
            dialog.getDialogPane().requestFocus();
        });

// Style the stage background
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.initStyle(StageStyle.UNDECORATED);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) return; // guard against empty input

            Category category = sceneManager.getAppConfig().getCategoryService()
                    .createCategory(name, CategoryType.valueOf(type));
            categoryBox.getItems().add(category);
            categoryBox.setValue(category);
            Toast.show(root, "Category created!"); // ← move INSIDE ifPresent
        });
// Toast.show removed from here
    }
    public void setType(String type) {
        this.type = type;
        loadCategories();

        if (currentTransaction != null) {
            categoryBox.getItems().stream().filter(c -> c.getId().equals(currentTransaction.getCategoryID())).findFirst().ifPresent(categoryBox::setValue);
        }
    }
    public void setTransaction(TransactionReportDTO transaction){
        this.currentTransaction = transaction;

        amountField.setText(transaction.getAmount().toString());
        datePicker.setValue(transaction.getDate());
        descrField.setText(transaction.getDescription());
        categoryBox.setValue(sceneManager.getAppConfig().getCategoryService().findById(transaction.getCategoryID()));
    }
    private void showValidationErrors(ValidationResult result){
        clearErrors();
        if(result.getError("amount")!=null){
            amountField.setStyle("-fx-border-color: red;");
            amountLabel.setText(result.getError("amount"));
            amountLabel.setVisible(true);
        }
        if(result.getError("category")!=null){
            categoryBox.setStyle("-fx-border-color: red;");
            categoryLabel.setText(result.getError("category"));
            categoryLabel.setVisible(true);
        }
        if(result.getError("date")!=null){
            datePicker.setStyle("-fx-border-color: red;");
            dateLabel.setText(result.getError("date"));
            dateLabel.setVisible(true);
        }
    }
    private void clearErrors(){
        amountField.setStyle("");
        amountLabel.setVisible(false);
        categoryBox.setStyle("");
        categoryLabel.setVisible(false);
        datePicker.setStyle("");
        dateLabel.setVisible(false);
    }
    private void formatAmountField(TextField amountField){
        amountField.textProperty().addListener((obs,oldVal,newVal)->{
            String digits = newVal.replaceAll("\\D","");

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
    private void setupDatePicker(DatePicker datePicker){
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Disable any date after today
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #1f2937; -fx-text-fill: #374151;");
                }
            }
        });
    }
}
