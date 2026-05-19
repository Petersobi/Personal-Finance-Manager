package com.peter.financeapp.ui.controller;

import com.peter.financeapp.service.CategoryService;
import com.peter.financeapp.service.TransactionService;
import com.peter.financeapp.util.SceneManager;
import javafx.scene.layout.StackPane;

public abstract class BaseController {
    protected SceneManager sceneManager;
    protected StackPane rootStack;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setRootStack(StackPane rootStack){
        this.rootStack = rootStack;
    }

}
