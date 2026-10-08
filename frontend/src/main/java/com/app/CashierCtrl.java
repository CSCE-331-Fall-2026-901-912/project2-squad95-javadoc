package com.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import com.app.controllers.*;
import com.app.models.*;

import java.net.URL;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.util.Callback;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

//maybe not needed
import java.util.List;

public class CashierCtrl extends Application implements Initializable {

    private static final ItemController itemController = new ItemController();

    // Note: fx:id and variable name *must match* because of JavaFX reflection 
    @FXML 
    private ListView<Integer> ordersList;
    final ObservableList<Integer> orderIds = FXCollections.observableArrayList();

    @FXML 
    private ListView<String> orderItemsList;
    ObservableList<String> itemIDs = FXCollections.observableArrayList();


    @FXML
    private TilePane menuItemsPane;
    ObservableList<ShopMenuItem> menuItems = FXCollections.observableArrayList();

    @FXML
    public void initialize1() {
        System.out.println(new java.io.File("items.csv").getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader("items.csv"))) {

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                String name = data[1];
                double price = Double.parseDouble(data[2]);

                Button button = new Button(name);

                button.setPrefSize(100, 60);
                button.setWrapText(true);

                button.setOnAction(event -> {
                    System.out.println("Selected: " + name);
                    System.out.println("Price: $" + price);
                });

                menuItemsPane.getChildren().add(button);
            }

        } catch (IOException e) {
            System.out.println("FAILED TO READ items.csv");
            e.printStackTrace();
        }
    }
    
    final static int TEMP_ITEMS_COUNT = 32;

    // temp
    private void loadMenuPane() {
        Task<List<Item>> task = new Task<>() {
            @Override protected List<Item> call() throws Exception {
                return itemController.getAll();
            }
        };
        task.setOnSucceeded(e -> populateMenuPane(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }
    private void populateMenuPane(List<Item> items) {
        menuItemsPane.getChildren().clear();
        for (Item item : items) {
            Button btn = new Button(item.name() + "\n$" + item.basePrice());
            menuItemsPane.getChildren().add(btn);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        // ordersList isn't available when start() is called, so
        // populate it here instead
        // TODO: find a better way to dynamically populate ordersList
        
        for (int i = 0; i < TEMP_ITEMS_COUNT; ++i) {
            orderIds.add(i);
        }
        // TODO phase 4: give actions to listview that switches the 
        // global context for other menus  
        // https://blueskyworkshop.com/topics/Java-Pages/fx-ListView-Basics/
        ordersList.setItems(orderIds);

        itemIDs.add(STYLESHEET_CASPIAN);
        itemIDs.add(STYLESHEET_MODENA);
        itemIDs.add("12am");
        orderItemsList.setItems(itemIDs);

        //initialize1();
        loadMenuPane();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // primaryStage.setTitle("Hello World!");
        // Button btn = new Button();
        // btn.setText("Say 'Hello World'");
        // btn.setOnAction(new EventHandler<ActionEvent>() {
 
        //     @Override
        //     public void handle(ActionEvent event) {
        //         System.out.println("Hello World!");
        //     }
        // });

        // // temporary items
        // ArrayList<String> items = new ArrayList<String>();
        // items.add("AHH");
        // items.add("Hello");
        // items.add("world!");
        
        // GridPane rootPane = new GridPane();

        // FlowPane orderTabPane = new FlowPane();

        // TilePane menuPane = new TilePane();

        // FlowPane itemTabPane = new FlowPane();
        
        // // colors for debugging
        // ArrayList<BackgroundFill> backgroundFills = new ArrayList<BackgroundFill>();
        // backgroundFills.add(new BackgroundFill(Color.ALICEBLUE, null, null));
        // backgroundFills.add(new BackgroundFill(Color.ANTIQUEWHITE, null, null));
        // backgroundFills.add(new BackgroundFill(Color.CADETBLUE, null, null));

        // orderTabPane.setBackground(new Background(backgroundFills.get(0)));
        // GridPane.setConstraints(orderTabPane, 0, 0, 1, 4);

        // menuPane.setBackground(new Background(backgroundFills.get(1)));
        // GridPane.setConstraints(menuPane, 1, 0, 1, 4);

        // itemTabPane.setBackground(new Background(backgroundFills.get(2)));
        // GridPane.setConstraints(itemTabPane, 2, 0, 1, 4);

        // double[] columnPercentWidths = new double[] {25, 50, 25};
        // for (double percent : columnPercentWidths) {
        //     ColumnConstraints columnProp = new ColumnConstraints();
        //     columnProp.setPercentWidth(percent);
        //     columnProp.setHgrow(Priority.ALWAYS);
        //     rootPane.getColumnConstraints().add(columnProp);
        // }

        // // programming war crime
        // ArrayList<String> orderIDs = new ArrayList<String>();
        // orderIDs.add(STYLESHEET_CASPIAN);
        // orderIDs.add(STYLESHEET_MODENA);
        // orderIDs.add("12am");

        // populateListPane(orderTabPane, orderIDs);
        // populateListPane(itemTabPane, items);
        
        // // Columns fill the entire window
        // RowConstraints rowProp = new RowConstraints();
        // // NOTE: adjust this if other panes (like a tab or info pane)
        // // are added to the top or bottom
        // rowProp.setPercentHeight(100);
        // rootPane.getRowConstraints().add(rowProp);

        // rootPane.getChildren().addAll(orderTabPane, menuPane, itemTabPane);

        // // Button button = new Button();
        // // button.setText("I love 331!");
        // // GridPane.setRowIndex(button, 0);
        // // GridPane.setColumnIndex(button, 1);

        // Label label = new Label();
        // label.setText(STYLESHEET_CASPIAN);

        // orderTabPane.getChildren().add(label);
        // menuPane.getChildren().add(label);
        // itemTabPane.getChildren().add(label);

        // // GridPane.setConstraints(label, 2, 0); // column=2 row=0

        // // var gridPaneChildren = rootPane.getChildren();

        // // gridPaneChildren.addAll(button, btn, label);
        // primaryStage.setScene(new Scene(rootPane, 640, 480));
        // primaryStage.show();
        
        // leave code commented above for now pls

        // Set the controller manually to avoid needing to respecify fx:controller 
        // attribute again for every .fxml import
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cashier.fxml"));
        loader.setController(new CashierCtrl());
        Parent root = loader.load();
    
        Scene scene = new Scene(root, 1280, 720);
    
        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    private static void showError(Throwable ex) {
        ex.printStackTrace();
    }
    private static void printResults(List<Item> items) {
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }
    public static void main(String[] args) {
        
        launch();
    }

}