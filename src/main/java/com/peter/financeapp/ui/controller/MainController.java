package com.peter.financeapp.ui.controller;

import com.peter.financeapp.util.SceneManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML private Button logoutBtn;
    @FXML private Button reportBtn;
    @FXML private Button budgetBtn;
    @FXML private Button categoryBtn;
    @FXML private Button transactionBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button toggleBtn;

    @FXML private StackPane contentArea;
    @FXML private StackPane root;
    @FXML private VBox sidebar;
    @FXML private HBox titleBar;
    @FXML private VBox loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    private Button activeBtn;

    private SceneManager sceneManager;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean collapsed = false;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        titleBar.setOnMouseDragged(e -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        // Show loading animation first, then load dashboard
        showLoadingThenLoad("/ui/fxml/dashboard-view.fxml");
        setActiveButton(dashboardBtn);
    }

    private void showLoadingThenLoad(String fxmlPath) {

        // Make sure loading pane is visible and on top
        loadingPane.setVisible(true);
        loadingPane.setOpacity(1);
        loadingPane.toFront();

        // Pulse animation on the ⚡ icon
        animatePulseIcon();

        // Wait 1.5 seconds then load the actual view
        PauseTransition pause = new PauseTransition(Duration.millis(1000));
        pause.setOnFinished(e -> {

            // Fade out loading pane
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), loadingPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                loadingPane.setVisible(false);
                loadView(fxmlPath); // load dashboard after fade
            });
            fadeOut.play();
        });
        pause.play();
    }

    private void animatePulseIcon() {
        // Find the pulse label
        loadingPane.getChildren().stream()
                .filter(n -> n instanceof javafx.scene.control.Label &&
                        "⚡".equals(((javafx.scene.control.Label) n).getText()))
                .findFirst()
                .ifPresent(node -> {

                    // Scale pulse on the icon
                    ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
                    scale.setFromX(1.0);
                    scale.setFromY(1.0);
                    scale.setToX(1.3);
                    scale.setToY(1.3);
                    scale.setAutoReverse(true);
                    scale.setCycleCount(Animation.INDEFINITE);
                    scale.play();

                    // Stop animation when loading is done
                    loadingPane.visibleProperty().addListener((obs, wasVisible, isVisible) -> {
                        if (!isVisible) scale.stop();
                    });
                });
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setSceneManager(sceneManager);
                baseController.setRootStack(root);
            }

            view.setOpacity(0);
            contentArea.getChildren().setAll(view);

            // Keep loading pane hidden
            if (contentArea.getChildren().contains(loadingPane)) {
                loadingPane.setVisible(false);
            }

            FadeTransition fade = new FadeTransition(Duration.millis(350), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void showDashBoard()    { loadView("/ui/fxml/dashboard-view.fxml");
        setActiveButton(dashboardBtn);}

    @FXML private void showTransactions() { loadView("/ui/fxml/transactions-view.fxml");
        setActiveButton(transactionBtn);}

    @FXML private void showCategories()   { loadView("/ui/fxml/categories-view.fxml");
        setActiveButton(categoryBtn);}

    @FXML private void showBudgets()      { loadView("/ui/fxml/budgets-view.fxml");
        setActiveButton(budgetBtn);}

    @FXML private void showReports()      { loadView("/ui/fxml/reports-view.fxml");
        setActiveButton(reportBtn);}

    @FXML
    private void toggleSidebar() {
        collapsed = !collapsed;
        double targetWidth = collapsed ? 60 : 220;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth))
        );
        timeline.play();

        updateButton(dashboardBtn, "Dashboard");
        updateButton(transactionBtn, "Transactions");
        updateButton(categoryBtn, "Categories");
        updateButton(budgetBtn, "Budgets");
        updateButton(reportBtn, "Reports");
    }

    private void updateButton(Button button, String text) {
        if (collapsed) {
            button.setText("");
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setAlignment(Pos.CENTER);
            Tooltip.install(button, new Tooltip(text));
        } else {
            button.setText(text);
            button.setContentDisplay(ContentDisplay.LEFT);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setTooltip(null);
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");

        ButtonType logoutButton = new ButtonType("Yes, Logout");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(logoutButton, cancelButton);

        // Style it
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/styles/app-theme.css").toExternalForm()
        );
        dialogPane.setStyle(
                "-fx-background-color: #1f2937;" +
                        "-fx-border-color: #374151;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-background-radius: 12 12 0 0;"
        );
        dialogPane.lookup(".header-panel .label").setStyle(
                "-fx-text-fill: #f9fafb;" +
                        "-fx-font-family: 'Syne Bold';" +
                        "-fx-font-size: 15px;"
        );
        dialogPane.lookup(".content.label").setStyle(
                "-fx-text-fill: #9ca3af;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-size: 13px;"
        );

        // Logout button — red
        dialogPane.lookupButton(logoutButton).setStyle(
                "-fx-background-color: linear-gradient(to right, #ef4444, #dc2626);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Nunito';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18;" +
                        "-fx-cursor: hand;"
        );

        // Cancel button — outline
        dialogPane.lookupButton(cancelButton).setStyle(
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

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == logoutButton) {
            Stage stage = (Stage) transactionBtn.getScene().getWindow();
            sceneManager.switchScene(stage, "/ui/fxml/login.fxml");
        }
    }

    @FXML
    private void handleMinimize() {
        ((Stage) titleBar.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void handleMaximize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void handleClose() {
        ((Stage) titleBar.getScene().getWindow()).close();
    }
    private void setActiveButton(Button button) {
        // Remove active style from previous button
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("sidebar-btn-active");
        }
        // Set new active button
        activeBtn = button;
        activeBtn.getStyleClass().add("sidebar-btn-active");
    }
}