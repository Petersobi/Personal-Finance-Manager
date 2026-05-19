package com.peter.financeapp.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


public class Toast {
    public static void show(StackPane root, String message) {
        if (root == null) {
            System.out.println("ERROR: Toast root is null!");
            return;
        }

        Label toast = new Label(message);
        toast.setStyle("""
            -fx-background-color: rgba(0, 0, 0, 0.9);
            -fx-text-fill: white;
            -fx-padding: 12 24;
            -fx-background-radius: 8;
            -fx-font-size: 14px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0, 0, 2);
            """);

        root.getChildren().add(toast);
        toast.toFront();

        StackPane.setAlignment(toast, Pos.CENTER);
        toast.setTranslateY(60);            // Start a bit lower


        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(250), toast);
        slideUp.setFromY(60);
        slideUp.setToY(0);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> root.getChildren().remove(toast));

        new SequentialTransition(fadeIn, slideUp, pause, fadeOut).play();

    }
}
