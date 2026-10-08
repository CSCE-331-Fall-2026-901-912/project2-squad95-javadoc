package com.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.net.URL;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import io.github.cdimascio.dotenv.Dotenv;

public class CashierCtrl extends Application implements Initializable {

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
    private Label orderPreviewLabel;

    private Button selectedDrink = null;

    private double selectedDrinkPrice = 0.0;
    private double toppingTotal = 0.0;

    private String selectedSugar = "100%";
    private String selectedIce = "100%";

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

                button.setStyle("-fx-background-color: lightgray;");

                button.setOnAction(event -> {

                    if (selectedDrink != null) {
                        selectedDrink.setStyle("-fx-background-color: lightgray;");
                    }

                    button.setStyle("-fx-background-color: lightgreen;");


                    selectedDrink = button;
                    selectedDrinkPrice = price;

                    System.out.println("Selected: " + name);
                    System.out.println("Price: $" + price);
                    
                    showToppingsPopup();
                });

                menuItemsPane.getChildren().add(button);
            }

        } catch (IOException e) {
            System.out.println("FAILED TO READ items.csv");
            e.printStackTrace();
        }
    }


    private void showToppingsPopup() {
        //System.out.println("POPUP STARTED");
        //double[] toppingTotal = {0.0};
        Stage popup = new Stage();
        popup.setTitle("Select Toppings");

        FlowPane toppingsPane = new FlowPane();
        toppingsPane.setHgap(10);
        toppingsPane.setVgap(10);
        toppingsPane.setPadding(new Insets(20));
        toppingsPane.setPrefWrapLength(400);

        Set<String> selectedToppings = new HashSet<>();
        double[] toppingTotal = {0.0};

        try (BufferedReader reader =
                new BufferedReader(new FileReader("toppings.csv"))) {

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                String name = data[1];
                double price = Double.parseDouble(data[2]);

                Button button = new Button(name);
                button.setPrefSize(120, 60);
                button.setWrapText(true);
                button.setStyle("-fx-background-color: lightgray;");

                button.setOnAction(event -> {
                    if (selectedToppings.contains(name)) {
                        selectedToppings.remove(name);
                        toppingTotal[0] -= price;
                        button.setStyle("-fx-background-color: lightgray;");
                    } else {
                        selectedToppings.add(name);
                        toppingTotal[0] += price;
                        button.setStyle("-fx-background-color: lightgreen;");
                    }
                });

                toppingsPane.getChildren().add(button);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(event -> {
            double totalPrice = selectedDrinkPrice + toppingTotal[0];

            String orderInfo = String.format("Drink: %s%n%n" + "Drink Price: $%.2f%n%n" + "Toppings: %s%n%n" + "Toppings Price: $%.2f%n%n" + "Total Price: $%.2f",
                selectedDrink.getText(), selectedDrinkPrice, selectedToppings, toppingTotal[0], totalPrice);

            orderPreviewLabel.setText(orderInfo);

            popup.close();

            showSugarPopup(selectedToppings, toppingTotal[0]);
        });

        VBox root = new VBox(20, toppingsPane, confirmButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 450);
        popup.setScene(scene);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.showAndWait();
    }


    private void showSugarPopup(Set<String> toppings, double toppingsPrice) {
        Stage popup = new Stage();
        popup.setTitle("Select Sugar Level");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Choose Sugar Level");

        String[] sugarLevels = {
            "0%", "25%", "50%", "75%", "100%"
        };

        ToggleGroup group = new ToggleGroup();

        for (String level : sugarLevels) {
            RadioButton button = new RadioButton(level);
            button.setToggleGroup(group);

            if (level.equals("100%")) {
                button.setSelected(true);
            }

            root.getChildren().add(button);
        }

        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(event -> {
            RadioButton selected =
                (RadioButton) group.getSelectedToggle();

            selectedSugar = selected.getText();

            popup.close();
            showIcePopup(toppings, toppingsPrice);
        });

        root.getChildren().add(0, title);
        root.getChildren().add(confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }


    private void showIcePopup(Set<String> toppings, double toppingsPrice) {
        Stage popup = new Stage();
        popup.setTitle("Select Ice Level");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Choose Ice Level");

        String[] iceLevels = {
            "No Ice", "Less Ice", "Regular Ice", "Extra Ice"
        };

        ToggleGroup group = new ToggleGroup();

        for (String level : iceLevels) {
            RadioButton button = new RadioButton(level);
            button.setToggleGroup(group);

            if (level.equals("Regular Ice")) {
                button.setSelected(true);
            }

            root.getChildren().add(button);
        }

        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(event -> {
            RadioButton selected =
                (RadioButton) group.getSelectedToggle();

            selectedIce = selected.getText();

            double totalPrice = selectedDrinkPrice + toppingsPrice;

            String orderInfo = String.format(
                "Drink: %s%n" +
                "Drink Price: $%.2f%n" +
                "Toppings: %s%n" +
                "Toppings Price: $%.2f%n" +
                "Sugar Level: %s%n" +
                "Ice Level: %s%n" +
                "Total Price: $%.2f",
                selectedDrink.getText(),
                selectedDrinkPrice,
                String.join(", ", toppings),
                toppingsPrice,
                selectedSugar,
                selectedIce,
                totalPrice
            );

            orderPreviewLabel.setText(orderInfo);

            popup.close();
        });

        root.getChildren().add(0, title);
        root.getChildren().add(confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }









    final static int TEMP_ITEMS_COUNT = 32;

    
    // temp
    private void populateMenuPane() {
        for (int i = 0; i < TEMP_ITEMS_COUNT / 2; ++i) {
            menuItems.add(new ShopMenuItem("GOgog fjfjfjfjfjfjfjfjfjfjfjffjf fjfjfj"));
            menuItems.add(new ShopMenuItem("Dusak"));
        }
        for (ShopMenuItem shopMenuItem : menuItems) {
            Button menuButton = new Button(shopMenuItem.name());
            menuButton.setPrefSize(60, 60);
            menuButton.setMaxSize(100, 100);
            menuButton.setWrapText(true);
            menuItemsPane.getChildren().add(menuButton);
            System.out.println(menuItemsPane.getChildren().size());
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

        initialize1();
        //initializeDrinks();
        //populateMenuPane();
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
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String dbHost = dotenv.get("DB_HOST");
        String dbUser = dotenv.get("DB_USER");
        String dbPass = dotenv.get("DB_PASSWORD");
        System.out.println(dbHost);
        launch();
    }

}