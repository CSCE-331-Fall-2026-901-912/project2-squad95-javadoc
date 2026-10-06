package com.app;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

        ArrayList<String> items = new ArrayList<String>();
        items.add("AHH");
        items.add("Hello");
        items.add("world!");
        
        GridPane rootPane = new GridPane();

        FlowPane orderTabPane = new FlowPane();

        TilePane menuPane = new TilePane();

        FlowPane itemTabPane = new FlowPane();
        
        // temporary colors
        ArrayList<BackgroundFill> backgroundFills = new ArrayList<BackgroundFill>();
        backgroundFills.add(new BackgroundFill(Color.ALICEBLUE, null, null));
        backgroundFills.add(new BackgroundFill(Color.ANTIQUEWHITE, null, null));
        backgroundFills.add(new BackgroundFill(Color.CADETBLUE, null, null));

        orderTabPane.setBackground(new Background(backgroundFills.get(0)));
        GridPane.setConstraints(orderTabPane, 0, 0, 1, 4);

        menuPane.setBackground(new Background(backgroundFills.get(1)));
        GridPane.setConstraints(menuPane, 1, 0, 1, 4);

        itemTabPane.setBackground(new Background(backgroundFills.get(2)));
        GridPane.setConstraints(itemTabPane, 2, 0, 1, 4);


        double[] columnPercentWidths = new double[] {25, 50, 25};
        for (double percent : columnPercentWidths) {
            ColumnConstraints columnProp = new ColumnConstraints();
            columnProp.setPercentWidth(percent);
            columnProp.setHgrow(Priority.ALWAYS);
            rootPane.getColumnConstraints().add(columnProp);
        }
        
        // Columns fill the entire window
        RowConstraints rowProp = new RowConstraints();
        // NOTE: adjust this if other panes (like a tab or info pane)
        // are added to the top or bottom
        rowProp.setPercentHeight(100);
        rootPane.getRowConstraints().add(rowProp);

        rootPane.getChildren().addAll(orderTabPane, menuPane, itemTabPane);

        // Button button = new Button();
        // button.setText("I love 331!");
        // GridPane.setRowIndex(button, 0);
        // GridPane.setColumnIndex(button, 1);

        Label label = new Label();
        label.setText(STYLESHEET_CASPIAN);

        orderTabPane.getChildren().add(label);
        menuPane.getChildren().add(label);
        itemTabPane.getChildren().add(label);

        // GridPane.setConstraints(label, 2, 0); // column=2 row=0

        // var gridPaneChildren = rootPane.getChildren();

        // gridPaneChildren.addAll(button, btn, label);
        primaryStage.setScene(new Scene(rootPane, 640, 480));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}