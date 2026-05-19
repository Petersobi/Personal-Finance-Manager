package com.peter.financeapp.ui;

import com.peter.financeapp.service.AppConfig;
import com.peter.financeapp.ui.controller.LoginController;
import com.peter.financeapp.util.DButil;
import com.peter.financeapp.util.SceneManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

    public void start(Stage stage)throws Exception{
        stage.initStyle(StageStyle.UNDECORATED);
        loadFont();

        DButil.initializeDataBase();
       AppConfig appConfig = new AppConfig();
       SceneManager sceneManager = new SceneManager(appConfig);
        LoginController controller = sceneManager.switchScene(stage,"/ui/fxml/login.fxml");
        controller.setSceneManager(sceneManager);
    }
     public static void main(String[] args) {
        launch();
    }
    private void loadFont(){
        Font.loadFont(getClass().getResourceAsStream("/fonts/Syne-Bold.ttf"),14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Nunito-Regular.ttf"),14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Nunito-Medium.ttf"),14);
    }
}
