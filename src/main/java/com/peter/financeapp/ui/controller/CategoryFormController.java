package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.util.SceneManager;
import com.peter.financeapp.util.Toast;
import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.validationForm.CategoryForm;
import com.peter.financeapp.util.validator.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CategoryFormController extends BaseController {
    @FXML
    private ComboBox<String> chooseCategoryType;
    @FXML
    private TextField nameField;

    @FXML private Label categoryLabel;
    @FXML private Label nameLabel;

    @FXML private StackPane root;

    private SceneManager sceneManager;
    private Category currentCategory;

    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;
        chooseCategoryType.getItems().setAll("INCOME","EXPENSE");
    }

    @Override
    public void setRootStack(StackPane rootStack) {
        super.setRootStack(rootStack);
    }

    public void setCurrentCategory(Category currentCategory){
        this.currentCategory = currentCategory;
        nameField.setText(currentCategory.getName());
        chooseCategoryType.setValue(currentCategory.getType().toString());
    }

    @FXML
    private void handleSave() {
        CategoryForm form = new CategoryForm();
        form.setCategoryName(nameField.getText());
        form.setCategoryType(chooseCategoryType.getValue());

        ValidationResult result = sceneManager.getAppConfig().getCategoryValidator().validate(form);

        if (result.hasErrors()){
            showValidationErrors(result);
            return;
        }
        try {
            if (currentCategory==null){
                sceneManager.getAppConfig().getCategoryService().createCategory(form.getCategoryName(), CategoryType.valueOf(form.getCategoryType()));
                Toast.show(rootStack,"Category saved!");}
            else {
                sceneManager.getAppConfig().getCategoryService().updateCategory(currentCategory.getId(), form.getCategoryName(), form.getCategoryType().toString());
                Toast.show(rootStack,"Category updated!");
            }
            closeWindow();
        } catch (ValidationException e) {
            Toast.show(root, e.getMessage());
        }

    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }
    private void closeWindow(){
        Stage stage=(Stage) chooseCategoryType.getScene().getWindow();
        stage.close();
    }
    private void showValidationErrors(ValidationResult result){
        clearErrors();
        if (result.getError("name")!=null){
            nameField.setStyle("-fx-border-color: red;");
            nameLabel.setText(result.getError("name"));
            nameLabel.setVisible(true);
        }
        if (result.getError("type")!= null){
            chooseCategoryType.setStyle("-fx-border-color: red;");
            categoryLabel.setText(result.getError("type"));
            categoryLabel.setVisible(true);

        }
    }
    private void clearErrors(){
        chooseCategoryType.setStyle("");
        categoryLabel.setVisible(false);
        nameField.setStyle("");
        nameLabel.setVisible(false);
    }
}
