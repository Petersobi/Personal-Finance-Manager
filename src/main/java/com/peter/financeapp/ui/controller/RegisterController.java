package com.peter.financeapp.ui.controller;

import com.peter.financeapp.service.AuthException;
import com.peter.financeapp.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private Label nameLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Label passwordLabel;
    @FXML private Label usernameLabel;

    @FXML private Button registerButton;

    @FXML private HBox titleBar;

    @FXML private PasswordField confirmPasswordField;
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
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
        }); }

    @FXML private void handleRegister() {

        clearError();
        if (firstNameField.getText().isEmpty()||lastNameField.getText().isEmpty()){
            nameLabel.setText("Name cannot be Empty");
            nameLabel.setVisible(true);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())){
            confirmPasswordLabel.setText("Password doesn't match");
            confirmPasswordLabel.setVisible(true);
            return;
        } else if (passwordField.getText().isEmpty()) {
            passwordLabel.setText("Password cannot be empty");
            passwordLabel.setVisible(true);
        } else if (usernameField.getText().isEmpty()) {
            passwordLabel.setText("username cannot be empty");
            passwordLabel.setVisible(true);
        }
        try {
            sceneManager.getAppConfig().getAuthService().register(usernameField.getText(), passwordField.getText(),firstNameField.getText(),lastNameField.getText());

            Stage stage = (Stage) registerButton.getScene().getWindow();
            LoginController controller =   sceneManager.switchScene(stage, "/ui/fxml/login.fxml");
            controller.setSceneManager(sceneManager);
        } catch (AuthException e) {
            if(e.getMessage().contains("password")){
                passwordLabel.setText(e.getMessage());
                passwordLabel.setVisible(true);
            }
            else if (e.getMessage().contains("username")) {
                usernameLabel.setText(e.getMessage());
                usernameLabel.setVisible(true);
            } else nameLabel.setText(e.getMessage());
        nameLabel.setVisible(true);}
    }

    @FXML private void gotoLogin() {

            Stage stage = (Stage) registerButton.getScene().getWindow();
            LoginController controller = sceneManager.switchScene(stage, "/ui/fxml/login.fxml");
            controller.setSceneManager(sceneManager);
    }
    private void clearError(){
        nameLabel.setVisible(false);
        usernameLabel.setVisible(false);
        passwordLabel.setVisible(false);
        confirmPasswordLabel.setVisible(false);
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
}
