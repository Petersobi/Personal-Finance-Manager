package com.peter.financeapp.util;

import com.peter.financeapp.service.AppConfig;
import com.peter.financeapp.ui.controller.BaseController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {
    private final AppConfig appConfig;
    private StackPane rootStack;

    public SceneManager(AppConfig appConfig){
        this.appConfig = appConfig;
    }

    public <T> T switchScene(Stage stage,String fxmlPath){
    try {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent rootNode = loader.load();

        StackPane rootPane = (StackPane)rootNode.lookup("#root");
        if (rootPane!=null){
                this.rootStack = rootPane;
        }


        Scene scene = new Scene(rootNode);


        scene.getStylesheets().add(SceneManager.class.getResource("/styles/app-theme.css").toExternalForm());

        boolean wasMaximized = stage.isMaximized();

        stage.setScene(scene);

        if (wasMaximized) {
            stage.setMaximized(true);
        }
        

        stage.setScene(scene);
        stage.show();


        return loader.getController();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }}

    public <T> T openModal(String fxmlPath, String title, Consumer<T> initializer){
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

            Parent root = loader.load();
            T controller = loader.getController();

            StackPane modalRoot = (StackPane) root.lookup("#root");

            if(controller instanceof BaseController baseController){
                baseController.setSceneManager(this);
                baseController.setRootStack(rootStack);

                if(modalRoot!=null){
                    this.rootStack = modalRoot;
                }

                if(initializer!=null){
                    initializer.accept(controller);
                }
            }
            Stage stage = new Stage();
            stage.setTitle(title);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource("/styles/app-theme.css").toExternalForm());

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }}

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public StackPane getRoot() {
        return rootStack;
    }

    public void setRoot(StackPane root) {
        this.rootStack = root;
    }
}
