package com.app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CashierCtrl extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        GridPane gridpane = new GridPane();
        Button button = new Button();
        GridPane.setRowIndex(button, 0);
        GridPane.setColumnIndex(button, 1);

        Label label = new Label();
        GridPane.setConstraints(label, 2, 0); // column=2 row=0

        gridpane.getChildren().addAll(button, label);
        primaryStage.setScene(new Scene(gridpane, 300, 250));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}