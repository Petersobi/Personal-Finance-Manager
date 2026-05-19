package com.peter.financeapp.ui.controller;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.Transaction;
import com.peter.financeapp.service.AuthException;
import com.peter.financeapp.service.AuthService;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;
import com.peter.financeapp.util.SceneManager;
import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.validationForm.LoginForm;
import com.peter.financeapp.util.validationForm.RegistrationForm;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;


public class LoginController {
    @FXML private Label usernameErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private HBox titleBar;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager){
        this.sceneManager = sceneManager;
    }



    @FXML public void initialize(){
        // Make window draggable from title bar
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        titleBar.setOnMouseDragged(e -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        errorLabel.setVisible(false);
    }
    @FXML private void handleLogin(){

        LoginForm form = new LoginForm();
        form.setUsername(usernameField.getText());
        form.setPassword(passwordField.getText());

        String username = form.getUsername();
        String password = form.getPassword();

        ValidationResult result = sceneManager.getAppConfig().getLoginValidator().validate(form);

        if (result.hasErrors()){
            showValidationErrors(result);
            return;
        }

        AuthService authService = sceneManager.getAppConfig().getAuthService();
        try {
        authService.login(username,password);

        errorLabel.setVisible(false);
            Stage stage = (Stage) loginButton.getScene().getWindow();

         MainController controller =   sceneManager.switchScene(stage, "/ui/fxml/main-layout.fxml");
         controller.setSceneManager(sceneManager);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        }
        catch (AuthException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    @FXML private void handleRegister(){
        Stage stage = (Stage) loginButton.getScene().getWindow();
        RegisterController controller =   sceneManager.switchScene(stage, "/ui/fxml/register.fxml");
        controller.setSceneManager(sceneManager);

    }



    private double xOffset = 0;
    private double yOffset = 0;


    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
    }
    private void showValidationErrors(ValidationResult result){
        clearErrors();
        if(result.getError("username")!=null){
            usernameField.setStyle("-fx-border-color: red;");
            usernameErrorLabel.setText(result.getError("username"));
            usernameErrorLabel.setVisible(true);
        }
        if(result.getError("password")!=null){
            passwordField.setStyle("-fx-border-color: red;");
            passwordErrorLabel.setText(result.getError("password"));
            passwordErrorLabel.setVisible(true);
        }
    }
    private void clearErrors(){
        usernameField.setStyle("");
        usernameErrorLabel.setVisible(false);
        passwordField.setStyle("");
        passwordErrorLabel.setVisible(false);
    }
}
