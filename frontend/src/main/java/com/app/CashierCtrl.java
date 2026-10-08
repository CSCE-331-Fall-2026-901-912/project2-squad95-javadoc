package com.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;

import com.app.controllers.*;
import com.app.models.*;

import java.util.Set;

import javafx.beans.value.*;

import java.net.URL;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.Node;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import io.github.cdimascio.dotenv.Dotenv;

//maybe not needed
import java.util.List;

public class CashierCtrl extends Application implements Initializable {

    private static final ItemController itemController = new ItemController();

    // Note: fx:id and variable name *must match* because of JavaFX reflection 
    @FXML 
    private ListView<Order> ordersList;
    // NOTE: cellfactory is an alternative but that's for phase 4/5
    final ObservableList<Order> orderEntries = FXCollections.observableArrayList();

    @FXML 
    private ListView<OrderItem> orderItemsList;

    @FXML 
    private Button newOrderButton;

    @FXML
    private Button checkoutButton;

    @FXML private Button deleteOrderButton;

    // the working id used for tracking the currently selected
    // order. this should be transformed into a different id
    // for inserting into the database when submitted
    private static int currProgOrderId = 0;

    // the id of the most recently created order. used for creating new orders
    private static int latestProgOrderId = 0; 

    @FXML
    private TilePane menuItemsPane;
    final ObservableList<ShopMenuItem> menuItems = FXCollections.observableArrayList();
    
    private void onNewOrderClick(ActionEvent event) {
        ObservableList<OrderItem> orderList = FXCollections.observableArrayList();
        ++latestProgOrderId;
        Order newOrder = new Order(latestProgOrderId, orderList);
        orderEntries.add(newOrder);
    }

    private void initializeOrderLists() {
        newOrderButton.setOnAction(this::onNewOrderClick);
        
        // https://stackoverflow.com/questions/13264017/getting-selected-element-from-listview 
        ordersList.getSelectionModel().selectedItemProperty().addListener( (observable, oldVal, newVal) -> {
            Order currSelectedOrder = newVal;
            // break early if no list item is selected
            if (currSelectedOrder == null) { 
                orderItemsList.setItems(null);
                return; 
            }

            currProgOrderId = currSelectedOrder.id();

            orderItemsList.setItems(currSelectedOrder.orderItems());
        });

        // TODO for phase 4: interaction for selecting an order's item 
    }

    private boolean removeFirstOrderItemOfId(int targetId) {
        // Linear search, because id =/= list index
        // would be better to use an observable map later on
        for (int i = 0; i < orderEntries.size(); ++i) {
            Order entry = orderEntries.get(i);
            if (entry.id() == targetId) {
                orderEntries.remove(i);
                return true;          
            }
        }

        String errorMessage = String.format("Failed to remove order of ID %d", currProgOrderId);
        System.err.println(errorMessage);
        return false;
    }

    private int getOrderIndexOfId(int targetId) {
        for (int i = 0; i < orderEntries.size(); ++i) {
            Order entry = orderEntries.get(i);
            if (entry.id() == targetId) {
                return i;
            }
        }

        String errorMessage = String.format("Failed to remove order of ID %d", currProgOrderId);
        System.err.println(errorMessage);
        return -1;
    }

    private void onCheckout(ActionEvent event) {
        // TODO: SQL submit command(s)

        removeFirstOrderItemOfId(currProgOrderId);
    }

    private void onDeleteOrder(ActionEvent event) {
        removeFirstOrderItemOfId(currProgOrderId);
    } 

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

            // TODO: orderitem doesn't fill in all req info, need to complete  
            OrderItem completedOrderItem = new OrderItem(selectedDrink.getText());
            addItemToCurrOrder(completedOrderItem);

            popup.close();
        });

        root.getChildren().add(0, title);
        root.getChildren().add(confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }

    private void addItemToCurrOrder(OrderItem item) {
        int currOrderIndex = getOrderIndexOfId(currProgOrderId);
        // handle case where no order is selected 
        if (currOrderIndex < 0) return;
        orderEntries.get(currOrderIndex).orderItems().add(item);
    }

    @FXML
    private void switchToManager(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/manager.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
        // TODO phase 4: give actions to listview that switches the 
        // global context for other menus  
        // https://blueskyworkshop.com/topics/Java-Pages/fx-ListView-Basics/
        ordersList.setItems(orderEntries);

        initialize1();
        //initializeDrinks();
        //populateMenuPane();

        // ordersList isn't available when start() is called, so
        // populate it here instead
        initializeOrderLists();

        checkoutButton.setOnAction(this::onCheckout);
        deleteOrderButton.setOnAction(this::onDeleteOrder);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the controller manually to avoid needing to respecify fx:controller 
        // attribute again for every .fxml import
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cashier.fxml"));
        //loader.setController(new CashierCtrl());
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