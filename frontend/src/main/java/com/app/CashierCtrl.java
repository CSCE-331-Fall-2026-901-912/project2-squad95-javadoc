package com.app;

import javafx.fxml.FXML;
// import java.io.IOException;
// import java.util.List;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
// import javafx.event.EventHandler;

// import java.net.*;
// import javafx.geometry.*;
// import javafx.scene.control.*;
// import javafx.scene.layout.*;
import javafx.scene.text.*;

public class CashierCtrl extends Application {

    @FXML private Text actiontarget;
    private int timesClicked = 0;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        ++timesClicked;
        actiontarget.setText("Sign in button pressed %d".formatted(timesClicked));
    }


    @Override
    public void start(Stage stage) throws Exception {
        // Unrelated: to refer to an fxml file from here, do "/file.fxml", because the 
        // "root" is automatically src/main/resources/

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        System.out.print("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        Parent root = FXMLLoader.load(getClass().getResource("/cashier.fxml"));

        stage.setTitle("Joson");

        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}