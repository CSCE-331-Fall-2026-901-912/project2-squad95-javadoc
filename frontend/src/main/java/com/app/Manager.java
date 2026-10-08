package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Manager extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/manager.fxml"));
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}